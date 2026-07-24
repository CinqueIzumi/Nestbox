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
 * Material scale ([NestboxTypography]) that Material components consume implicitly. Display roles use
 * [displaySerifFontFamily] (Instrument Serif), reading roles use [bodySerifFontFamily] (Newsreader),
 * and the developer-native accent roles (kicker, meta, code, stat) use [monoFontFamily] (Spline Sans
 * Mono). Role names carried over from the prior sans/mono system keep their names even where their
 * family and size changed; see `docs/design-system.md` for the full role table.
 */
@Immutable
data class ReaderTypography(
    val masthead: TextStyle,
    val kicker: TextStyle,
    val kickerSmall: TextStyle,

    val pageTitle: TextStyle,
    val headline: TextStyle,
    val headlineSmall: TextStyle,

    val articleTitle: TextStyle,
    val feedTitle: TextStyle,
    val identityName: TextStyle,
    val avatarInitial: TextStyle,
    val dropCap: TextStyle,

    val bodyLarge: TextStyle,
    val body: TextStyle,
    val bodySmall: TextStyle,
    val rowTitle: TextStyle,
    val readerNote: TextStyle,

    val meta: TextStyle,
    val metaStrong: TextStyle,
    val identifierSmall: TextStyle,
    val identifier: TextStyle,
    val badgeGlyph: TextStyle,
    val chipLabel: TextStyle,
    val tabLabel: TextStyle,
    val controlLabel: TextStyle,
    val code: TextStyle,

    val statNumber: TextStyle,
    val pullQuote: TextStyle,
)

val DefaultReaderTypography: ReaderTypography = ReaderTypography(
    masthead = TextStyle(
        fontFamily = displaySerifFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 34.sp,
        lineHeight = 34.sp,
    ),
    kicker = TextStyle(
        fontFamily = monoFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 10.sp,
        lineHeight = 14.sp,
        letterSpacing = 2.2.sp,
    ),
    kickerSmall = TextStyle(
        fontFamily = monoFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 9.5.sp,
        lineHeight = 13.sp,
        letterSpacing = 1.9.sp,
    ),

    pageTitle = TextStyle(
        fontFamily = displaySerifFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 30.sp,
        lineHeight = 33.sp,
    ),
    headline = TextStyle(
        fontFamily = displaySerifFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 31.sp,
        lineHeight = 35.sp,
    ),
    headlineSmall = TextStyle(
        fontFamily = displaySerifFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 24.sp,
        lineHeight = 29.sp,
    ),

    articleTitle = TextStyle(
        fontFamily = displaySerifFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 17.5.sp,
        lineHeight = 22.sp,
    ),
    feedTitle = TextStyle(
        fontFamily = displaySerifFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp,
        lineHeight = 18.sp,
    ),
    identityName = TextStyle(
        fontFamily = displaySerifFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 18.sp,
        lineHeight = 22.sp,
    ),
    avatarInitial = TextStyle(
        fontFamily = displaySerifFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 22.sp,
    ),
    dropCap = TextStyle(
        fontFamily = displaySerifFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 54.sp,
        lineHeight = 44.sp,
    ),

    bodyLarge = TextStyle(
        fontFamily = bodySerifFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 17.sp,
        lineHeight = 29.sp,
    ),
    body = TextStyle(
        fontFamily = bodySerifFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 17.sp,
        lineHeight = 29.sp,
    ),
    bodySmall = TextStyle(
        fontFamily = bodySerifFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 15.5.sp,
        lineHeight = 25.sp,
    ),
    rowTitle = TextStyle(
        fontFamily = bodySerifFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 15.5.sp,
        lineHeight = 25.sp,
    ),
    readerNote = TextStyle(
        fontFamily = bodySerifFontFamily,
        fontWeight = FontWeight.Normal,
        fontStyle = FontStyle.Italic,
        fontSize = 14.5.sp,
        lineHeight = 22.sp,
    ),

    meta = TextStyle(
        fontFamily = monoFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 9.sp,
        lineHeight = 13.sp,
        letterSpacing = 1.26.sp,
    ),
    metaStrong = TextStyle(
        fontFamily = monoFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 9.sp,
        lineHeight = 13.sp,
        letterSpacing = 1.08.sp,
    ),
    identifierSmall = TextStyle(
        fontFamily = monoFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 9.5.sp,
        lineHeight = 13.sp,
        letterSpacing = 0.76.sp,
    ),
    identifier = TextStyle(
        fontFamily = monoFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 11.sp,
        lineHeight = 15.sp,
    ),
    badgeGlyph = TextStyle(
        fontFamily = monoFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 10.sp,
        lineHeight = 14.sp,
    ),
    chipLabel = TextStyle(
        fontFamily = monoFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 10.sp,
        lineHeight = 14.sp,
        letterSpacing = 1.4.sp,
    ),
    tabLabel = TextStyle(
        fontFamily = monoFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 10.sp,
        lineHeight = 14.sp,
        letterSpacing = 1.2.sp,
    ),
    controlLabel = TextStyle(
        fontFamily = monoFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 10.5.sp,
        lineHeight = 15.sp,
        letterSpacing = 1.68.sp,
    ),
    code = TextStyle(
        fontFamily = monoFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 21.sp,
    ),

    statNumber = TextStyle(
        fontFamily = monoFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 40.sp,
        lineHeight = 44.sp,
    ),
    pullQuote = TextStyle(
        fontFamily = displaySerifFontFamily,
        fontWeight = FontWeight.Normal,
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
