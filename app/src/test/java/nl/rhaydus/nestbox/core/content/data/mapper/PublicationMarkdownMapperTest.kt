package nl.rhaydus.nestbox.core.content.data.mapper

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class PublicationMarkdownMapperTest {
    private val mapper = PublicationMarkdownMapper()

    @Nested
    inner class ParseHeader {
        @Test
        fun `extracts number, date, and type marker from the header line`() {
            // ----- Arrange -----
            val markdown = "## #7 2026-06-12 Weekly\n\nIntro paragraph."

            // ----- Act -----
            val header = mapper.parseHeader(markdown)

            // ----- Assert -----
            assertEquals(
                Triple(
                    7,
                    "2026-06-12",
                    "Weekly",
                ),
                header,
            )
        }

        @Test
        fun `returns null when there is no publication header`() {
            // ----- Arrange -----
            val markdown = "## Article & References\n\nNo publication header here."

            // ----- Act -----
            val header = mapper.parseHeader(markdown)

            // ----- Assert -----
            assertNull(header)
        }
    }

    @Nested
    inner class ExtractPreview {
        @Test
        fun `returns the first paragraph after the header collapsed to one line`() {
            // ----- Arrange -----
            val markdown = """
                ## #1 2026-06-12 Weekly

                First line of the intro
                second line of the intro.

                ## Section
            """.trimIndent()

            // ----- Act -----
            val preview = mapper.extractPreview(markdown)

            // ----- Assert -----
            assertEquals(
                "First line of the intro second line of the intro.",
                preview,
            )
        }

        @Test
        fun `keeps the paragraph whole when a heading sits between two prose lines`() {
            // ----- Arrange -----
            val markdown = """
                First line of the intro
                ## Section
                second line of the intro.

                Later paragraph.
            """.trimIndent()

            // ----- Act -----
            val preview = mapper.extractPreview(markdown)

            // ----- Assert -----
            assertEquals(
                "First line of the intro second line of the intro.",
                preview,
            )
        }

        @Test
        fun `returns empty when the markdown holds no prose`() {
            // ----- Arrange -----
            val markdown = """
                ## #1 2026-06-12 Weekly

                ## Section
            """.trimIndent()

            // ----- Act -----
            val preview = mapper.extractPreview(markdown)

            // ----- Assert -----
            assertEquals(
                "",
                preview,
            )
        }
    }

    @Nested
    inner class StripHeader {
        @Test
        fun `removes the leading publication header line`() {
            // ----- Arrange -----
            val markdown = "## #1 2026-06-12 Weekly\n\nBody paragraph."

            // ----- Act -----
            val result = mapper.stripHeader(markdown)

            // ----- Assert -----
            assertEquals(
                "Body paragraph.",
                result,
            )
        }
    }
}
