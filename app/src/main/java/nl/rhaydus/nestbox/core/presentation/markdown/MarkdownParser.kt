package nl.rhaydus.nestbox.core.presentation.markdown

import nl.rhaydus.nestbox.core.presentation.markdown.model.MarkdownBlock
import nl.rhaydus.nestbox.core.presentation.markdown.model.MarkdownInline
import org.intellij.markdown.MarkdownElementTypes
import org.intellij.markdown.MarkdownTokenTypes
import org.intellij.markdown.ast.ASTNode
import org.intellij.markdown.ast.findChildOfType
import org.intellij.markdown.ast.getTextInNode
import org.intellij.markdown.flavours.commonmark.CommonMarkFlavourDescriptor
import org.intellij.markdown.parser.MarkdownParser as IntellijMarkdownParser

class MarkdownParser {
    private val flavour = CommonMarkFlavourDescriptor()

    fun parse(markdown: String): List<MarkdownBlock> {
        val root = IntellijMarkdownParser(flavour).buildMarkdownTreeFromString(markdown)

        val blocks = mutableListOf<MarkdownBlock>()

        root.children.forEach { node -> collectBlocks(node, markdown, depth = 0, into = blocks) }

        return blocks
    }

    private fun collectBlocks(
        node: ASTNode,
        text: String,
        depth: Int,
        into: MutableList<MarkdownBlock>,
    ) {
        when (node.type) {
            in HEADING_LEVELS.keys -> {
                val content = node.findChildOfType(MarkdownTokenTypes.ATX_CONTENT)
                val inlines = content?.let { trimEnds(parseInlines(it, text)) }.orEmpty()

                into += MarkdownBlock.Heading(level = HEADING_LEVELS.getValue(node.type), inlines = inlines)
            }

            MarkdownElementTypes.PARAGRAPH ->
                into += MarkdownBlock.Paragraph(trimEnds(parseInlines(node, text)))

            MarkdownElementTypes.UNORDERED_LIST, MarkdownElementTypes.ORDERED_LIST ->
                node.children
                    .filter { it.type == MarkdownElementTypes.LIST_ITEM }
                    .forEach { item -> collectListItem(item, text, depth, into) }

            MarkdownElementTypes.CODE_FENCE ->
                into += parseCodeFence(node, text)

            MarkdownElementTypes.BLOCK_QUOTE ->
                into += MarkdownBlock.Quote(trimEnds(parseQuoteInlines(node, text)))

            else -> Unit
        }
    }

    private fun collectListItem(
        item: ASTNode,
        text: String,
        depth: Int,
        into: MutableList<MarkdownBlock>,
    ) {
        val paragraph = item.children.firstOrNull { it.type == MarkdownElementTypes.PARAGRAPH }
        val inlines = trimEnds(parseInlines(paragraph ?: item, text))

        into += MarkdownBlock.BulletItem(depth = depth, inlines = inlines)

        item.children
            .filter { it.type == MarkdownElementTypes.UNORDERED_LIST || it.type == MarkdownElementTypes.ORDERED_LIST }
            .forEach { nested ->
                nested.children
                    .filter { it.type == MarkdownElementTypes.LIST_ITEM }
                    .forEach { child -> collectListItem(child, text, depth + 1, into) }
            }
    }

    private fun parseCodeFence(node: ASTNode, text: String): MarkdownBlock.CodeBlock {
        val raw = node.getTextInNode(text).toString()

        // Slice the raw text between the opening and closing fence lines so interior blank lines
        // survive (the per-line CODE_FENCE_CONTENT tokens drop them).
        val language = raw.substringBefore('\n').trimStart('`', '~').trim().ifEmpty { null }
        val code = raw.substringAfter('\n', "").substringBeforeLast('\n', "")

        return MarkdownBlock.CodeBlock(code = code, language = language)
    }

    private fun parseQuoteInlines(node: ASTNode, text: String): List<MarkdownInline> {
        val paragraphs = node.children.filter { it.type == MarkdownElementTypes.PARAGRAPH }

        if (paragraphs.isEmpty()) {
            return parseInlines(node, text)
        }

        val inlines = mutableListOf<MarkdownInline>()

        paragraphs.forEachIndexed { index, paragraph ->
            if (index > 0) {
                inlines += MarkdownInline.Text(" ")
            }

            inlines += parseInlines(paragraph, text)
        }

        return inlines
    }

    private fun parseInlines(node: ASTNode, text: String): List<MarkdownInline> {
        val out = mutableListOf<MarkdownInline>()

        node.children.forEach { child ->
            when (child.type) {
                MarkdownElementTypes.STRONG ->
                    out += MarkdownInline.Bold(parseInlines(child, text))

                MarkdownElementTypes.EMPH ->
                    out += parseInlines(child, text)

                MarkdownElementTypes.CODE_SPAN ->
                    out += MarkdownInline.Code(codeSpanText(child, text))

                MarkdownElementTypes.INLINE_LINK ->
                    parseLink(child, text)?.let { out += it }

                MarkdownTokenTypes.EOL, MarkdownTokenTypes.WHITE_SPACE ->
                    out += MarkdownInline.Text(" ")

                in INLINE_SKIP -> Unit

                else ->
                    if (child.children.isEmpty()) {
                        out += MarkdownInline.Text(child.getTextInNode(text).toString())
                    } else {
                        out += parseInlines(child, text)
                    }
            }
        }

        return coalesce(out)
    }

    // The marker tokens (backticks) sit as children alongside the content, so drop them and keep the rest.
    private fun codeSpanText(node: ASTNode, text: String): String =
        node.children
            .filter { it.type != MarkdownTokenTypes.BACKTICK }
            .joinToString(separator = "") { it.getTextInNode(text).toString() }
            .trim()

    private fun parseLink(node: ASTNode, text: String): MarkdownInline.Link? {
        val destination = node.findChildOfType(MarkdownElementTypes.LINK_DESTINATION) ?: return null

        val url = destination.getTextInNode(text).toString().trim().removeSurrounding("<", ">")
        val label = node.findChildOfType(MarkdownElementTypes.LINK_TEXT)
            ?.getTextInNode(text)
            ?.toString()
            ?.trim()
            ?.removeSurrounding("[", "]")
            ?: url

        return MarkdownInline.Link(inlines = listOf(MarkdownInline.Text(label)), url = url)
    }

    private fun coalesce(inlines: List<MarkdownInline>): List<MarkdownInline> {
        val out = mutableListOf<MarkdownInline>()

        inlines.forEach { inline ->
            val last = out.lastOrNull()

            if (inline is MarkdownInline.Text && last is MarkdownInline.Text) {
                out[out.lastIndex] = MarkdownInline.Text(last.text + inline.text)
            } else {
                out += inline
            }
        }

        return out
    }

    private fun trimEnds(inlines: List<MarkdownInline>): List<MarkdownInline> {
        if (inlines.isEmpty()) {
            return inlines
        }

        val trimmed = inlines.toMutableList()
        val first = trimmed.first()
        val last = trimmed.last()

        if (first is MarkdownInline.Text) {
            trimmed[0] = MarkdownInline.Text(first.text.trimStart())
        }

        if (last is MarkdownInline.Text) {
            trimmed[trimmed.lastIndex] = MarkdownInline.Text((trimmed.last() as MarkdownInline.Text).text.trimEnd())
        }

        return trimmed.filterNot { it is MarkdownInline.Text && it.text.isEmpty() }
    }

    private companion object {
        val HEADING_LEVELS = mapOf(
            MarkdownElementTypes.ATX_1 to 1,
            MarkdownElementTypes.ATX_2 to 2,
            MarkdownElementTypes.ATX_3 to 3,
            MarkdownElementTypes.ATX_4 to 4,
            MarkdownElementTypes.ATX_5 to 5,
            MarkdownElementTypes.ATX_6 to 6,
        )

        // Marker tokens and already-handled block containers that must not leak into inline text.
        val INLINE_SKIP = setOf(
            MarkdownTokenTypes.EMPH,
            MarkdownTokenTypes.LIST_BULLET,
            MarkdownTokenTypes.BLOCK_QUOTE,
            MarkdownElementTypes.UNORDERED_LIST,
            MarkdownElementTypes.ORDERED_LIST,
            MarkdownElementTypes.CODE_FENCE,
        )
    }
}
