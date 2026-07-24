package nl.rhaydus.nestbox.core.presentation.markdown

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import nl.rhaydus.nestbox.core.presentation.markdown.model.MarkdownBlock
import nl.rhaydus.nestbox.core.presentation.markdown.model.MarkdownInline
import nl.rhaydus.nestbox.core.presentation.theme.nestboxColors
import nl.rhaydus.nestbox.core.presentation.theme.readerTypography

private const val INLINE_CODE_ANNOTATION_TAG = "inlineCode"

/**
 * Renders parsed markdown into the reading view's [LazyListScope] (design system §3.5), mapping each
 * block to a [readerTypography] role. Lazy so the reader's scroll-progress bar and `READING · NN%`
 * chrome label (`PublicationDetailScreen`) can derive live position from the same `LazyListState`.
 * Link taps are surfaced through [onLinkClick] rather than opened here, so the off-app handoff happens
 * at the screen's `Content` (design system §5). H2s are numbered in document order with a mono accent
 * index.
 */
fun LazyListScope.markdownDocument(
    blocks: List<MarkdownBlock>,
    onLinkClick: (String) -> Unit,
) {
    var headingOrdinal = 0

    blocks.forEachIndexed { index, block ->
        val orderIndex = if (block is MarkdownBlock.Heading && block.level == 2) {
            headingOrdinal += 1
            headingOrdinal
        } else {
            null
        }

        item {
            if (index > 0) {
                Spacer(modifier = Modifier.height(gapBefore(
                    blocks[index - 1],
                    block,
                ),),)
            }

            when (block) {
                is MarkdownBlock.Heading -> HeadingBlock(
                    block = block,
                    orderIndex = orderIndex,
                    onLinkClick = onLinkClick,
                )

                is MarkdownBlock.Paragraph -> ParagraphBlock(
                    block = block,
                    onLinkClick = onLinkClick,
                )

                is MarkdownBlock.BulletItem -> BulletBlock(
                    block = block,
                    onLinkClick = onLinkClick,
                )

                is MarkdownBlock.CodeBlock -> CodeBlock(block = block)

                is MarkdownBlock.Quote -> QuoteBlock(
                    block = block,
                    onLinkClick = onLinkClick,
                )
            }
        }
    }
}

/**
 * The flattened plain text of an inline run, ignoring styling and link targets. Used by
 * `PublicationDetailScreen` to detect a leading `# Title` heading that only restates
 * `PublicationDetailUiState.title` so it isn't drawn twice.
 */
fun markdownPlainText(inlines: List<MarkdownInline>): String = buildString {
    inlines.forEach { inline ->
        when (inline) {
            is MarkdownInline.Text -> append(inline.text)
            is MarkdownInline.Bold -> append(markdownPlainText(inline.inlines))
            is MarkdownInline.Code -> append(inline.text)
            is MarkdownInline.Link -> append(markdownPlainText(inline.inlines))
        }
    }
}

@Composable
private fun HeadingBlock(
    block: MarkdownBlock.Heading,
    orderIndex: Int?,
    onLinkClick: (String) -> Unit,
) {
    if (orderIndex != null) {
        Row(verticalAlignment = Alignment.Top) {
            Text(
                text = orderIndex.toString().padStart(
                    2,
                    '0',
                ),
                style = MaterialTheme.readerTypography.kicker,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .padding(end = 10.dp)
                    .offset(y = (-6).dp),
            )

            MarkdownText(
                inlines = block.inlines,
                onLinkClick = onLinkClick,
                style = MaterialTheme.readerTypography.articleTitle,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }

        return
    }

    val style = when (block.level) {
        1 -> MaterialTheme.readerTypography.pageTitle
        3 -> MaterialTheme.readerTypography.headlineSmall
        4 -> MaterialTheme.readerTypography.articleTitle
        else -> MaterialTheme.readerTypography.feedTitle
    }

    MarkdownText(
        inlines = block.inlines,
        onLinkClick = onLinkClick,
        style = style,
        color = MaterialTheme.colorScheme.onSurface,
    )
}

@Composable
private fun ParagraphBlock(
    block: MarkdownBlock.Paragraph,
    onLinkClick: (String) -> Unit,
) {
    MarkdownText(
        inlines = block.inlines,
        onLinkClick = onLinkClick,
        style = MaterialTheme.readerTypography.body,
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
            style = MaterialTheme.readerTypography.bodySmall,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.width(20.dp),
        )

        MarkdownText(
            inlines = block.inlines,
            onLinkClick = onLinkClick,
            style = MaterialTheme.readerTypography.bodySmall,
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
        comment = MaterialTheme.colorScheme.outline,
        plain = MaterialTheme.colorScheme.inverseOnSurface,
    )
    val highlighted = remember(block.code, block.language, colors) {
        CodeHighlighter.highlight(
            block.code,
            block.language,
            colors,
        )
    }

    Surface(
        color = MaterialTheme.nestboxColors.codeSurface,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(
            text = highlighted,
            style = MaterialTheme.readerTypography.code,
            color = MaterialTheme.colorScheme.inverseOnSurface,
            softWrap = false,
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 18.dp, vertical = 16.dp),
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

        MarkdownText(
            inlines = block.inlines,
            onLinkClick = onLinkClick,
            style = MaterialTheme.readerTypography.pullQuote,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(start = 18.dp),
        )
    }
}

/**
 * Renders an inline run and its inline-code washes (design system: inline code on
 * `surfaceContainerHigh`). Drawn from [TextLayoutResult.getPathForRange] rather than
 * `SpanStyle.background`: a `SpanStyle` background rect is positioned from that span's own font
 * metrics, but this reader mixes a smaller mono run (the `code` role) inside a larger body-serif
 * paragraph, and the two disagree about where the shared line box sits — the rect painted from the
 * span's own metrics lands roughly a line below the glyphs Android actually places within the
 * paragraph's (taller) line. Reading the wash geometry back off the real `TextLayoutResult` after
 * layout sidesteps the mismatch entirely: it draws exactly where the glyphs were placed, regardless
 * of theme or system font scale, instead of guessing from metrics that don't govern placement.
 */
@Composable
private fun MarkdownText(
    inlines: List<MarkdownInline>,
    onLinkClick: (String) -> Unit,
    style: TextStyle,
    color: Color,
    modifier: Modifier = Modifier,
) {
    val text = inlineText(
        inlines,
        onLinkClick,
    )
    var layoutResult by remember { mutableStateOf<TextLayoutResult?>(null) }
    val inlineCodeWash = MaterialTheme.colorScheme.surfaceContainerHigh

    Text(
        text = text,
        style = style,
        color = color,
        onTextLayout = { layoutResult = it },
        modifier = modifier.drawBehind {
            val result = layoutResult ?: return@drawBehind

            text.getStringAnnotations(
                INLINE_CODE_ANNOTATION_TAG,
                0,
                text.length,
            ).forEach { annotation ->
                drawPath(
                    path = result.getPathForRange(
                        annotation.start,
                        annotation.end,
                    ),
                    color = inlineCodeWash,
                )
            }
        },
    )
}

@Composable
private fun inlineText(
    inlines: List<MarkdownInline>,
    onLinkClick: (String) -> Unit,
): AnnotatedString {
    val codeSpanStyle = MaterialTheme.readerTypography.code.toSpanStyle()
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
                appendInlines(
                    inline.inlines,
                    codeSpanStyle,
                    linkStyles,
                    onLinkClick,
                )
            }

            is MarkdownInline.Code -> {
                val start = length

                append(inline.text)
                addStyle(
                    codeSpanStyle,
                    start,
                    length,
                )
                addStringAnnotation(
                    INLINE_CODE_ANNOTATION_TAG,
                    "",
                    start,
                    length,
                )
            }

            is MarkdownInline.Link -> {
                val link = LinkAnnotation.Clickable(
                    tag = inline.url,
                    styles = linkStyles,
                    linkInteractionListener = { onLinkClick(inline.url) },
                )

                withLink(link) {
                    appendInlines(
                        inline.inlines,
                        codeSpanStyle,
                        linkStyles,
                        onLinkClick,
                    )
                }
            }
        }
    }
}

private fun gapBefore(
    previous: MarkdownBlock,
    next: MarkdownBlock,
): Dp = when {
    next is MarkdownBlock.Heading && next.level == 2 -> 30.dp
    previous is MarkdownBlock.Heading && previous.level == 2 -> 10.dp
    next is MarkdownBlock.Heading -> 28.dp
    previous is MarkdownBlock.Heading -> 16.dp
    previous is MarkdownBlock.BulletItem && next is MarkdownBlock.BulletItem -> 8.dp
    else -> 16.dp
}
