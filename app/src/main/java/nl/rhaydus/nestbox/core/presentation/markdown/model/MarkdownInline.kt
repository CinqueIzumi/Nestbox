package nl.rhaydus.nestbox.core.presentation.markdown.model

sealed interface MarkdownInline {

    data class Text(val text: String) : MarkdownInline

    data class Bold(val inlines: List<MarkdownInline>) : MarkdownInline

    data class Code(val text: String) : MarkdownInline

    data class Link(
        val inlines: List<MarkdownInline>,
        val url: String,
    ) : MarkdownInline
}
