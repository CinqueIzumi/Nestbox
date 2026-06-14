package nl.rhaydus.nestbox.core.presentation.markdown

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle

/**
 * Restrained, scheme-toned syntax tinting for the reading view's code blocks (design system §2.1):
 * keywords, strings, numbers, and comments only, drawn from the scheme tones supplied in [CodeColors],
 * never a rainbow palette. Tokenising is regex-based and runs left to right; everything a rule does
 * not claim stays in the plain code colour.
 */
object CodeHighlighter {

    fun highlight(
        code: String,
        language: String?,
        colors: CodeColors,
    ): AnnotatedString = buildAnnotatedString {
        val regex = regexFor(language)
        var cursor = 0

        regex.findAll(code).forEach { match ->
            if (match.range.first > cursor) {
                withStyle(SpanStyle(color = colors.plain)) {
                    append(code.substring(cursor, match.range.first))
                }
            }

            withStyle(SpanStyle(color = colorFor(match, colors))) {
                append(match.value)
            }

            cursor = match.range.last + 1
        }

        if (cursor < code.length) {
            withStyle(SpanStyle(color = colors.plain)) {
                append(code.substring(cursor))
            }
        }
    }

    private fun colorFor(match: MatchResult, colors: CodeColors): Color = when {
        match.groups[GROUP_COMMENT] != null -> colors.comment
        match.groups[GROUP_STRING] != null -> colors.string
        match.groups[GROUP_NUMBER] != null -> colors.number
        else -> colors.keyword
    }

    private fun regexFor(language: String?): Regex = when (language?.lowercase()) {
        "java" -> javaRegex
        "sh", "shell", "bash", "console" -> shellRegex
        else -> kotlinRegex
    }

    private fun buildRegex(commentPattern: String, keywords: List<String>): Regex {
        val keywordGroup = "@\\w+|\\b(?:${keywords.joinToString(separator = "|")})\\b"

        return Regex("($commentPattern)|($STRING_PATTERN)|($NUMBER_PATTERN)|($keywordGroup)")
    }

    private val kotlinRegex by lazy { buildRegex(SLASH_COMMENT, KOTLIN_KEYWORDS) }
    private val javaRegex by lazy { buildRegex(SLASH_COMMENT, JAVA_KEYWORDS) }
    private val shellRegex by lazy { buildRegex(HASH_COMMENT, SHELL_KEYWORDS) }

    // The four top-level capturing groups of the master regex, in order. Everything inside each
    // alternative is non-capturing, so these indices stay stable.
    private const val GROUP_COMMENT = 1
    private const val GROUP_STRING = 2
    private const val GROUP_NUMBER = 3

    private const val SLASH_COMMENT = "//[^\\n]*|/\\*[\\s\\S]*?\\*/"
    private const val HASH_COMMENT = "#[^\\n]*"
    private const val STRING_PATTERN = "\"(?:\\\\.|[^\"\\\\])*\"|'(?:\\\\.|[^'\\\\])*'"
    private const val NUMBER_PATTERN = "\\b\\d[\\d_]*(?:\\.\\d+)?\\b"

    private val KOTLIN_KEYWORDS = listOf(
        "val", "var", "fun", "class", "object", "interface", "if", "else", "when", "for", "while",
        "do", "return", "is", "in", "as", "by", "import", "package", "private", "public", "internal",
        "protected", "override", "open", "abstract", "sealed", "data", "enum", "companion", "init",
        "constructor", "this", "super", "null", "true", "false", "try", "catch", "finally", "throw",
        "suspend", "lateinit", "const", "vararg", "get", "set", "typealias",
    )

    private val JAVA_KEYWORDS = listOf(
        "class", "interface", "enum", "public", "private", "protected", "static", "final", "abstract",
        "void", "int", "long", "double", "float", "boolean", "char", "byte", "short", "new", "return",
        "if", "else", "for", "while", "do", "switch", "case", "break", "continue", "this", "super",
        "null", "true", "false", "try", "catch", "finally", "throw", "throws", "import", "package",
        "extends", "implements", "instanceof",
    )

    private val SHELL_KEYWORDS = listOf(
        "if", "then", "else", "elif", "fi", "for", "while", "do", "done", "case", "esac", "function",
        "echo", "export", "local", "return", "in", "true", "false",
    )
}
