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

    fun extractPreview(markdown: String): String {
        val paragraph = mutableListOf<String>()

        for (raw in markdown.lineSequence()) {
            val line = raw.trim()

            if (line.startsWith("#")) {
                continue
            }

            if (line.isEmpty()) {
                if (paragraph.isEmpty()) {
                    continue
                }

                break
            }

            paragraph += line
        }

        return paragraph.joinToString(separator = " ")
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
