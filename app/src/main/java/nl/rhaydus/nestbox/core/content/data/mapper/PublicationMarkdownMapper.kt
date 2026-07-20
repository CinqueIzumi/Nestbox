package nl.rhaydus.nestbox.core.content.data.mapper

class PublicationMarkdownMapper {
    fun parseHeader(markdown: String): Triple<Int, String, String>? {
        val match = HEADER_REGEX.find(markdown) ?: return null

        val (number, date, marker) = match.destructured

        return Triple(
            number.toInt(),
            date,
            marker,
        )
    }

    // The preview is the publication's first prose paragraph, collapsed onto one line.
    fun extractPreview(markdown: String): String {
        // Headings are never part of the preview, wherever they sit - dropping them first means a
        // heading between two prose lines doesn't split the paragraph.
        val prose = markdown.lineSequence()
            .map { it.trim() }
            .filterNot { it.startsWith("#") }

        // Blank lines before the paragraph are padding; the first blank line after it starts ends it.
        return prose
            .dropWhile { it.isEmpty() }
            .takeWhile { it.isNotEmpty() }
            .joinToString(separator = " ")
    }

    // Drops the header line; the reading view renders that metadata from its own styled header.
    fun stripHeader(markdown: String): String =
        markdown.replaceFirst(
            HEADER_REGEX,
            "",
        ).trimStart()

    private companion object {
        val HEADER_REGEX = Regex("""##\s+#(\d+)\s+(\d{4}-\d{2}-\d{2})\s+(\w+)""")
    }
}
