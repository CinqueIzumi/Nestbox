package nl.rhaydus.nestbox.core.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * The project-specific type scale used for any text a *screen itself* composes, distinct from the
 * Material scale ([NestboxTypography]) that Material components consume implicitly. Reading roles
 * use the sans [readerFontFamily]. The developer-native accent roles (kicker, meta, code, stat) use
 * the [monoFontFamily]. The mono face is monospaced by nature, so digits in [statNumber] and
 * [metaStrong] stay column-aligned without a tabular-figures feature.
 */
@Immutable
data class ReaderTypography(
    val kicker: TextStyle,
    val kickerSmall: TextStyle,

    val pageTitle: TextStyle,
    val headline: TextStyle,
    val headlineSmall: TextStyle,

    val articleTitle: TextStyle,
    val feedTitle: TextStyle,

    val bodyLarge: TextStyle,
    val body: TextStyle,
    val bodySmall: TextStyle,

    val meta: TextStyle,
    val metaStrong: TextStyle,
    val code: TextStyle,

    val statNumber: TextStyle,
    val pullQuote: TextStyle,
)

val DefaultReaderTypography: ReaderTypography = ReaderTypography(
    kicker = TextStyle(
        fontFamily = monoFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 2.sp,
    ),
    kickerSmall = TextStyle(
        fontFamily = monoFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 14.sp,
        letterSpacing = 1.5.sp,
    ),

    pageTitle = TextStyle(
        fontFamily = readerFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 30.sp,
        lineHeight = 36.sp,
    ),
    headline = TextStyle(
        fontFamily = readerFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 34.sp,
        letterSpacing = (-0.5).sp,
    ),
    headlineSmall = TextStyle(
        fontFamily = readerFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
    ),

    articleTitle = TextStyle(
        fontFamily = readerFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp,
        lineHeight = 24.sp,
    ),
    feedTitle = TextStyle(
        fontFamily = readerFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 15.sp,
        lineHeight = 20.sp,
    ),

    bodyLarge = TextStyle(
        fontFamily = readerFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 18.sp,
        lineHeight = 29.sp,
    ),
    body = TextStyle(
        fontFamily = readerFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 26.sp,
    ),
    bodySmall = TextStyle(
        fontFamily = readerFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 13.sp,
        lineHeight = 18.sp,
    ),

    meta = TextStyle(
        fontFamily = monoFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.2.sp,
    ),
    metaStrong = TextStyle(
        fontFamily = monoFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.2.sp,
    ),
    code = TextStyle(
        fontFamily = monoFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
    ),

    statNumber = TextStyle(
        fontFamily = monoFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 40.sp,
        lineHeight = 44.sp,
    ),
    pullQuote = TextStyle(
        fontFamily = readerFontFamily,
        fontWeight = FontWeight.Medium,
        fontStyle = FontStyle.Italic,
        fontSize = 20.sp,
        lineHeight = 28.sp,
    ),
)

internal val LocalReaderTypography = staticCompositionLocalOf { DefaultReaderTypography }

val MaterialTheme.readerTypography: ReaderTypography
    @Composable
    @ReadOnlyComposable
    get() = LocalReaderTypography.current
