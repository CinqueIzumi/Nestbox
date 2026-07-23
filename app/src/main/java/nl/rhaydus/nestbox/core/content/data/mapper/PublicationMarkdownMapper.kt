package nl.rhaydus.nestbox.core.content.data.mapper

import nl.rhaydus.nestbox.core.content.data.model.PublicationHeader

class PublicationMarkdownMapper {
    /**
     * The `## #7 2026-06-12 Weekly` line, read from the **first** line only. A document that merely
     * quotes a header further down (the repository README advertises the latest issue that way) must
     * not be mistaken for that publication.
     */
    fun parseHeader(markdown: String): PublicationHeader? {
        val firstLine = markdown.lineSequence().firstOrNull { line -> line.isNotBlank() } ?: return null

        val match = HEADER_REGEX.matchEntire(firstLine.trim()) ?: return null

        val (number, date, marker) = match.destructured

        return PublicationHeader(
            number = number.toInt(),
            date = date,
            marker = marker,
        )
    }

    /** The `date:` field of a YAML frontmatter block, which is how articles and interviews date themselves. */
    fun parseFrontmatterDate(markdown: String): String? {
        val lines = markdown.lines()

        if (lines.firstOrNull()?.trim() != FRONTMATTER_FENCE) return null

        return lines
            .drop(1)
            .takeWhile { line -> line.trim() != FRONTMATTER_FENCE }
            .firstNotNullOfOrNull { line -> FRONTMATTER_DATE_REGEX.find(line)?.groupValues?.get(1) }
    }

    /**
     * The publication's first line of readable content, collapsed onto one line.
     *
     * Publications do not all open with prose: a weekly letter's first content is a link list item,
     * and several open with an HTML banner image. Markup that would read as noise in a one-line
     * preview (frontmatter, headings, HTML blocks, list markers, link and image syntax) is therefore
     * removed rather than shown raw.
     */
    fun extractPreview(markdown: String): String {
        val prose = withoutFrontmatter(markdown)
            .lineSequence()
            .map { line -> line.trim() }
            .filterNot { line -> line.startsWith(HEADING_MARKER) || line.startsWith(HTML_MARKER) }
            .map { line -> toPlainText(line) }

        // Blank lines before the first content are padding; the next one ends the paragraph.
        return prose
            .dropWhile { line -> line.isEmpty() }
            .takeWhile { line -> line.isNotEmpty() }
            .joinToString(separator = " ")
    }

    /** Drops frontmatter and the header line; the reading view renders that metadata itself. */
    fun stripMetadata(markdown: String): String {
        val content = withoutFrontmatter(markdown)
        val firstLine = content.lineSequence().firstOrNull()?.trim()

        if (firstLine == null || HEADER_REGEX.matchEntire(firstLine) == null) return content

        return content.substringAfter(
            delimiter = '\n',
            missingDelimiterValue = "",
        ).trimStart()
    }

    private fun withoutFrontmatter(markdown: String): String {
        val lines = markdown.lines()

        if (lines.firstOrNull()?.trim() != FRONTMATTER_FENCE) return markdown

        val closingIndex = lines.drop(1).indexOfFirst { line -> line.trim() == FRONTMATTER_FENCE }

        if (closingIndex < 0) return markdown

        return lines
            .drop(closingIndex + FENCE_LINE_COUNT)
            .joinToString(separator = "\n")
            .trimStart()
    }

    private fun toPlainText(line: String): String = line
        .replace(
            LIST_MARKER_REGEX,
            "",
        )
        .replace(
            IMAGE_REGEX,
            "",
        )
        .replace(LINK_REGEX) { match -> match.groupValues[1] }
        .trim()

    private companion object {
        const val FRONTMATTER_FENCE = "---"
        const val HEADING_MARKER = "#"
        const val HTML_MARKER = "<"

        // The opening fence plus the closing one, both dropped to reach the content.
        const val FENCE_LINE_COUNT = 2

        val HEADER_REGEX = Regex("""##\s+#(\d+)\s+(\d{4}-\d{2}-\d{2})\s+(\w+)""")
        val FRONTMATTER_DATE_REGEX = Regex("""^date:\s*"?(\d{4}-\d{2}-\d{2})"?""")
        val LIST_MARKER_REGEX = Regex("""^\s*(?:[-*+]|\d+\.)\s+""")
        val IMAGE_REGEX = Regex("""!\[[^\]]*]\([^)]*\)""")
        val LINK_REGEX = Regex("""\[([^\]]+)]\([^)]*\)""")
    }
}
