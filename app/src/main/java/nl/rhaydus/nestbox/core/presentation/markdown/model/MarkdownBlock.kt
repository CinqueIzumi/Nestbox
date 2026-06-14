package nl.rhaydus.nestbox.core.presentation.markdown.model

sealed interface MarkdownBlock {

    data class Heading(
        val level: Int,
        val inlines: List<MarkdownInline>,
    ) : MarkdownBlock

    data class Paragraph(val inlines: List<MarkdownInline>) : MarkdownBlock

    data class BulletItem(
        val depth: Int,
        val inlines: List<MarkdownInline>,
    ) : MarkdownBlock

    data class CodeBlock(
        val code: String,
        val language: String?,
    ) : MarkdownBlock

    data class Quote(val inlines: List<MarkdownInline>) : MarkdownBlock
}
