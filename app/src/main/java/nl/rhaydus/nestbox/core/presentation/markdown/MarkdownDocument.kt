package nl.rhaydus.nestbox.core.presentation.markdown

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import nl.rhaydus.nestbox.core.presentation.markdown.model.MarkdownBlock
import nl.rhaydus.nestbox.core.presentation.markdown.model.MarkdownInline
import nl.rhaydus.nestbox.core.presentation.theme.readerTypography

/**
 * Renders parsed markdown for the reading view (design system §3.5), mapping each block to a
 * [readerTypography] role. Link taps are surfaced through [onLinkClick] rather than opened here, so
 * the off-app handoff happens at the screen's `Content` (design system §5).
 */
@Composable
fun MarkdownDocument(
    blocks: List<MarkdownBlock>,
    onLinkClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val leadParagraphIndex = blocks.indexOfFirst { it is MarkdownBlock.Paragraph }

    Column(modifier = modifier) {
        blocks.forEachIndexed { index, block ->
            if (index > 0) {
                Spacer(modifier = Modifier.height(gapBefore(blocks[index - 1], block)))
            }

            when (block) {
                is MarkdownBlock.Heading -> HeadingBlock(block = block, onLinkClick = onLinkClick)

                is MarkdownBlock.Paragraph -> ParagraphBlock(
                    block = block,
                    isLead = index == leadParagraphIndex,
                    onLinkClick = onLinkClick,
                )

                is MarkdownBlock.BulletItem -> BulletBlock(block = block, onLinkClick = onLinkClick)

                is MarkdownBlock.CodeBlock -> CodeBlock(block = block)

                is MarkdownBlock.Quote -> QuoteBlock(block = block, onLinkClick = onLinkClick)
            }
        }
    }
}

@Composable
private fun HeadingBlock(
    block: MarkdownBlock.Heading,
    onLinkClick: (String) -> Unit,
) {
    val style = when (block.level) {
        1 -> MaterialTheme.readerTypography.pageTitle
        2 -> MaterialTheme.readerTypography.headline
        3 -> MaterialTheme.readerTypography.headlineSmall
        4 -> MaterialTheme.readerTypography.articleTitle
        else -> MaterialTheme.readerTypography.feedTitle
    }

    Text(
        text = inlineText(block.inlines, onLinkClick),
        style = style,
        color = MaterialTheme.colorScheme.onSurface,
    )
}

@Composable
private fun ParagraphBlock(
    block: MarkdownBlock.Paragraph,
    isLead: Boolean,
    onLinkClick: (String) -> Unit,
) {
    Text(
        text = inlineText(block.inlines, onLinkClick),
        style = if (isLead) MaterialTheme.readerTypography.bodyLarge else MaterialTheme.readerTypography.body,
        color = MaterialTheme.colorScheme.onSurface,
    )
}

@Composable
private fun BulletBlock(
    block: MarkdownBlock.BulletItem,
    onLinkClick: (String) -> Unit,
) {
    Row(
        verticalAlignment = Alignment.Top,
        modifier = Modifier.padding(start = (block.depth * 16).dp),
    ) {
        Text(
            text = "•",
            style = MaterialTheme.readerTypography.body,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.width(20.dp),
        )

        Text(
            text = inlineText(block.inlines, onLinkClick),
            style = MaterialTheme.readerTypography.body,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
private fun CodeBlock(block: MarkdownBlock.CodeBlock) {
    val colors = CodeColors(
        keyword = MaterialTheme.colorScheme.primary,
        string = MaterialTheme.colorScheme.tertiary,
        number = MaterialTheme.colorScheme.secondary,
        comment = MaterialTheme.colorScheme.onSurfaceVariant,
        plain = MaterialTheme.colorScheme.onSurface,
    )
    val highlighted = remember(block.code, block.language, colors) {
        CodeHighlighter.highlight(block.code, block.language, colors)
    }

    Surface(
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(
            text = highlighted,
            style = MaterialTheme.readerTypography.code,
            softWrap = false,
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .padding(16.dp),
        )
    }
}

@Composable
private fun QuoteBlock(
    block: MarkdownBlock.Quote,
    onLinkClick: (String) -> Unit,
) {
    Row(modifier = Modifier.height(IntrinsicSize.Min)) {
        Spacer(
            modifier = Modifier
                .width(3.dp)
                .fillMaxHeight()
                .clip(RoundedCornerShape(2.dp))
                .background(MaterialTheme.colorScheme.primary),
        )

        Text(
            text = inlineText(block.inlines, onLinkClick),
            style = MaterialTheme.readerTypography.pullQuote,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(start = 16.dp),
        )
    }
}

@Composable
private fun inlineText(
    inlines: List<MarkdownInline>,
    onLinkClick: (String) -> Unit,
): AnnotatedString {
    val codeSpanStyle = MaterialTheme.readerTypography.code
        .toSpanStyle()
        .copy(background = MaterialTheme.colorScheme.surfaceContainerHigh)
    val linkStyles = TextLinkStyles(style = SpanStyle(color = MaterialTheme.colorScheme.primary))

    return buildAnnotatedString {
        appendInlines(
            inlines = inlines,
            codeSpanStyle = codeSpanStyle,
            linkStyles = linkStyles,
            onLinkClick = onLinkClick,
        )
    }
}

private fun AnnotatedString.Builder.appendInlines(
    inlines: List<MarkdownInline>,
    codeSpanStyle: SpanStyle,
    linkStyles: TextLinkStyles,
    onLinkClick: (String) -> Unit,
) {
    inlines.forEach { inline ->
        when (inline) {
            is MarkdownInline.Text -> append(inline.text)

            is MarkdownInline.Bold -> withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                appendInlines(inline.inlines, codeSpanStyle, linkStyles, onLinkClick)
            }

            is MarkdownInline.Code -> withStyle(codeSpanStyle) {
                append(inline.text)
            }

            is MarkdownInline.Link -> {
                val link = LinkAnnotation.Clickable(
                    tag = inline.url,
                    styles = linkStyles,
                    linkInteractionListener = { onLinkClick(inline.url) },
                )

                withLink(link) {
                    appendInlines(inline.inlines, codeSpanStyle, linkStyles, onLinkClick)
                }
            }
        }
    }
}

private fun gapBefore(previous: MarkdownBlock, next: MarkdownBlock): Dp = when {
    next is MarkdownBlock.Heading -> 28.dp
    previous is MarkdownBlock.Heading -> 16.dp
    previous is MarkdownBlock.BulletItem && next is MarkdownBlock.BulletItem -> 8.dp
    else -> 16.dp
}
