package nl.rhaydus.nestbox.core.presentation.markdown

import androidx.compose.ui.graphics.Color
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class CodeHighlighterTest {
    private val colors = CodeColors(
        keyword = Color.Red,
        string = Color.Green,
        number = Color.Blue,
        comment = Color.Gray,
        plain = Color.Black,
    )

    private fun colorOf(
        code: String,
        language: String?,
        token: String,
    ): Color? {
        val result = CodeHighlighter.highlight(
            code,
            language,
            colors,
        )

        return result.spanStyles
            .firstOrNull { result.text.substring(
                it.start,
                it.end,
            ) == token }
            ?.item
            ?.color
    }

    @Nested
    inner class Kotlin {
        @Test
        fun `colors keywords, numbers, strings, and comments by scheme role`() {
            // ----- Arrange -----
            val code = "val name = \"hi\" // greet\nval count = 10"

            // ----- Act & Assert -----
            assertEquals(
                Color.Red,
                colorOf(
                    code,
                    "kotlin",
                    "val",
                ),
            )
            assertEquals(
                Color.Blue,
                colorOf(
                    code,
                    "kotlin",
                    "10",
                ),
            )
            assertEquals(
                Color.Green,
                colorOf(
                    code,
                    "kotlin",
                    "\"hi\"",
                ),
            )
            assertEquals(
                Color.Gray,
                colorOf(
                    code,
                    "kotlin",
                    "// greet",
                ),
            )
        }
    }

    @Nested
    inner class Shell {
        @Test
        fun `treats hash as a comment`() {
            // ----- Arrange -----
            val code = "echo hi # a note"

            // ----- Act -----
            val commentColor = colorOf(
                code,
                "shell",
                "# a note",
            )

            // ----- Assert -----
            assertEquals(
                Color.Gray,
                commentColor,
            )
        }
    }
}
