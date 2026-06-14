package nl.rhaydus.nestbox.core.presentation.markdown

import nl.rhaydus.nestbox.core.presentation.markdown.model.MarkdownBlock
import nl.rhaydus.nestbox.core.presentation.markdown.model.MarkdownInline
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class MarkdownParserTest {

    private val parser = MarkdownParser()

    @Nested
    inner class Headings {

        @Test
        fun `maps atx levels to heading blocks`() {
            // ----- Arrange -----
            val markdown = "## Two\n\n### Three\n\n#### Four"

            // ----- Act -----
            val headings = parser.parse(markdown).filterIsInstance<MarkdownBlock.Heading>()

            // ----- Assert -----
            assertEquals(listOf(2, 3, 4), headings.map { it.level })
            assertEquals("Two", plainText(headings.first().inlines))
        }
    }

    @Nested
    inner class InlineFormatting {

        @Test
        fun `parses bold, inline code, and a link in one paragraph`() {
            // ----- Arrange -----
            val markdown = "Text with **bold**, `code`, and a [label](https://example.com/x)."

            // ----- Act -----
            val paragraph = parser.parse(markdown).filterIsInstance<MarkdownBlock.Paragraph>().single()

            // ----- Assert -----
            assertTrue(paragraph.inlines.any { it is MarkdownInline.Bold })
            assertTrue(paragraph.inlines.any { it is MarkdownInline.Code && it.text == "code" })

            val link = paragraph.inlines.filterIsInstance<MarkdownInline.Link>().single()
            assertEquals("https://example.com/x", link.url)
            assertEquals("label", plainText(link.inlines))
        }
    }

    @Nested
    inner class Lists {

        @Test
        fun `flattens nested bullets with increasing depth`() {
            // ----- Arrange -----
            val markdown = """
                - Parent
                  - Child one
                  - Child two
            """.trimIndent()

            // ----- Act -----
            val bullets = parser.parse(markdown).filterIsInstance<MarkdownBlock.BulletItem>()

            // ----- Assert -----
            assertEquals(3, bullets.size)
            assertEquals(0, bullets[0].depth)
            assertEquals("Parent", plainText(bullets[0].inlines))
            assertEquals(1, bullets[1].depth)
            assertEquals(1, bullets[2].depth)
        }
    }

    @Nested
    inner class CodeFence {

        @Test
        fun `keeps the language and interior blank lines`() {
            // ----- Arrange -----
            val markdown = "```kotlin\nval a = 1\n\nval b = 2\n```"

            // ----- Act -----
            val code = parser.parse(markdown).filterIsInstance<MarkdownBlock.CodeBlock>().single()

            // ----- Assert -----
            assertEquals("kotlin", code.language)
            assertEquals("val a = 1\n\nval b = 2", code.code)
        }
    }

    @Nested
    inner class Quote {

        @Test
        fun `parses blockquote text without the marker`() {
            // ----- Arrange -----
            val markdown = "> A quoted line."

            // ----- Act -----
            val quote = parser.parse(markdown).filterIsInstance<MarkdownBlock.Quote>().single()

            // ----- Assert -----
            assertEquals("A quoted line.", plainText(quote.inlines))
        }
    }

    private fun plainText(inlines: List<MarkdownInline>): String =
        inlines.joinToString(separator = "") { inline ->
            when (inline) {
                is MarkdownInline.Text -> inline.text
                is MarkdownInline.Bold -> plainText(inline.inlines)
                is MarkdownInline.Code -> inline.text
                is MarkdownInline.Link -> plainText(inline.inlines)
            }
        }
}
