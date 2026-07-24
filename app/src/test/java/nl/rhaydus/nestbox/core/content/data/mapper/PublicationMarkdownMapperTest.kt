package nl.rhaydus.nestbox.core.content.data.mapper

import nl.rhaydus.nestbox.core.content.data.model.PublicationHeader
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
                PublicationHeader(
                    number = 7,
                    date = "2026-06-12",
                    marker = "Weekly",
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

        @Test
        fun `ignores a header quoted further down, as the README advertises the latest issue`() {
            // ----- Arrange -----
            val markdown = "# Dove Letter\n\nA subscription repository.\n\n## #105 2026-07-20 Weekly"

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
    inner class ExtractPreviewFromRealShapes {
        @Test
        fun `collapses a weekly letter's first link list item to readable text`() {
            // ----- Arrange -----
            val markdown = """
                ## #104 2026-07-13 Weekly

                ## 📚 Article & References

                - [Mirage: Cloudy Grows into a Graphics Effect Library](https://proandroiddev.com/mirage): Mirage has expanded its capabilities.
            """.trimIndent()

            // ----- Act -----
            val preview = mapper.extractPreview(markdown)

            // ----- Assert -----
            assertEquals(
                "Mirage: Cloudy Grows into a Graphics Effect Library: Mirage has expanded its capabilities.",
                preview,
            )
        }

        @Test
        fun `skips an HTML banner and starts at the prose`() {
            // ----- Arrange -----
            val markdown = "<img src=\"https://example.com/banner.png\" width=\"23%\"/>\n\nThe actual opening line."

            // ----- Act -----
            val preview = mapper.extractPreview(markdown)

            // ----- Assert -----
            assertEquals(
                "The actual opening line.",
                preview,
            )
        }

        @Test
        fun `skips frontmatter so an article preview starts at its prose`() {
            // ----- Arrange -----
            val markdown = "---\ndate: 2026-07-07\ntags: [\"Compose\"]\n---\n\n# How an AI Agent Builds UI\n\nAI agents can now reason about a task."

            // ----- Act -----
            val preview = mapper.extractPreview(markdown)

            // ----- Assert -----
            assertEquals(
                "AI agents can now reason about a task.",
                preview,
            )
        }
    }

    @Nested
    inner class StripMetadata {
        @Test
        fun `removes the leading publication header line`() {
            // ----- Arrange -----
            val markdown = "## #1 2026-06-12 Weekly\n\nBody paragraph."

            // ----- Act -----
            val result = mapper.stripMetadata(markdown)

            // ----- Assert -----
            assertEquals(
                "Body paragraph.",
                result,
            )
        }

        @Test
        fun `removes a frontmatter block so the reader never renders raw YAML`() {
            // ----- Arrange -----
            val markdown = "---\ndate: 2026-07-07\ntags: [\"Compose\"]\n---\n\n# Title\n\nBody."

            // ----- Act -----
            val result = mapper.stripMetadata(markdown)

            // ----- Assert -----
            assertEquals(
                "# Title\n\nBody.",
                result,
            )
        }
    }

    @Nested
    inner class ParseTitle {
        @Test
        fun `returns the quoted frontmatter title`() {
            // ----- Arrange -----
            val markdown = """
                ---
                title: "Quoted Title"
                date: 2026-07-07
                ---

                # Different Heading

                Body.
            """.trimIndent()

            // ----- Act -----
            val title = mapper.parseTitle(markdown)

            // ----- Assert -----
            assertEquals(
                "Quoted Title",
                title,
            )
        }

        @Test
        fun `returns the bare frontmatter title`() {
            // ----- Arrange -----
            val markdown = """
                ---
                title: Bare Title
                date: 2026-07-07
                ---

                # Different Heading

                Body.
            """.trimIndent()

            // ----- Act -----
            val title = mapper.parseTitle(markdown)

            // ----- Assert -----
            assertEquals(
                "Bare Title",
                title,
            )
        }

        @Test
        fun `falls back to the first level-1 heading after frontmatter when there is no frontmatter title`() {
            // ----- Arrange -----
            val markdown = "---\ndate: 2026-07-07\ntags: [\"Compose\"]\n---\n\n# How an AI Agent Builds UI\n\nAI agents can now reason about a task."

            // ----- Act -----
            val title = mapper.parseTitle(markdown)

            // ----- Assert -----
            assertEquals(
                "How an AI Agent Builds UI",
                title,
            )
        }

        @Test
        fun `falls back to the level-1 heading when there is no frontmatter at all`() {
            // ----- Arrange -----
            val markdown = "# Title Without Frontmatter\n\nBody paragraph."

            // ----- Act -----
            val title = mapper.parseTitle(markdown)

            // ----- Assert -----
            assertEquals(
                "Title Without Frontmatter",
                title,
            )
        }

        @Test
        fun `returns null for a weekly letter, which carries no level-1 heading`() {
            // ----- Arrange -----
            val markdown = """
                ## #7 2026-06-12 Weekly

                - [Some Link](https://example.com): Description.
            """.trimIndent()

            // ----- Act -----
            val title = mapper.parseTitle(markdown)

            // ----- Assert -----
            assertNull(title)
        }

        @Test
        fun `never mistakes a level-2 heading for a title`() {
            // ----- Arrange -----
            val markdown = """
                ## Section Heading

                Body paragraph.
            """.trimIndent()

            // ----- Act -----
            val title = mapper.parseTitle(markdown)

            // ----- Assert -----
            assertNull(title)
        }

        @Test
        fun `strips markdown link syntax from the heading, mirroring extractPreview`() {
            // ----- Arrange -----
            val markdown = "# [Mirage: Cloudy Grows into a Graphics Effect Library](https://proandroiddev.com/mirage)"

            // ----- Act -----
            val title = mapper.parseTitle(markdown)

            // ----- Assert -----
            assertEquals(
                "Mirage: Cloudy Grows into a Graphics Effect Library",
                title,
            )
        }
    }

    @Nested
    inner class ParseFrontmatterDate {
        @Test
        fun `reads the date an article or interview dates itself with`() {
            // ----- Arrange -----
            val markdown = "---\ndate: 2026-07-07\nunlocked: true\n---\n\n# Title"

            // ----- Act -----
            val date = mapper.parseFrontmatterDate(markdown)

            // ----- Assert -----
            assertEquals(
                "2026-07-07",
                date,
            )
        }

        @Test
        fun `returns null when the publication has no frontmatter`() {
            // ----- Arrange -----
            val markdown = "## #1 2026-06-12 Weekly\n\nBody paragraph."

            // ----- Act -----
            val date = mapper.parseFrontmatterDate(markdown)

            // ----- Assert -----
            assertNull(date)
        }
    }
}
