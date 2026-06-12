package nl.rhaydus.nestbox.core.presentation.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.unit.sp

private val baseline = Typography()

/**
 * The Material type scale consumed implicitly by off-the-shelf Material 3 components such as top-bar
 * titles, button labels, and navigation labels. It is the standard scale set in the reading face.
 * The screen-composed editorial roles live in [ReaderTypography]. The body roles get a touch more
 * line height than the Material default so running prose reads comfortably.
 */
val NestboxTypography: Typography = Typography(
    displayLarge = baseline.displayLarge.copy(fontFamily = readerFontFamily),
    displayMedium = baseline.displayMedium.copy(fontFamily = readerFontFamily),
    displaySmall = baseline.displaySmall.copy(fontFamily = readerFontFamily),
    headlineLarge = baseline.headlineLarge.copy(fontFamily = readerFontFamily),
    headlineMedium = baseline.headlineMedium.copy(fontFamily = readerFontFamily),
    headlineSmall = baseline.headlineSmall.copy(fontFamily = readerFontFamily),
    titleLarge = baseline.titleLarge.copy(fontFamily = readerFontFamily),
    titleMedium = baseline.titleMedium.copy(fontFamily = readerFontFamily),
    titleSmall = baseline.titleSmall.copy(fontFamily = readerFontFamily),
    bodyLarge = baseline.bodyLarge.copy(
        fontFamily = readerFontFamily,
        lineHeight = 26.sp,
    ),
    bodyMedium = baseline.bodyMedium.copy(fontFamily = readerFontFamily),
    bodySmall = baseline.bodySmall.copy(fontFamily = readerFontFamily),
    labelLarge = baseline.labelLarge.copy(fontFamily = readerFontFamily),
    labelMedium = baseline.labelMedium.copy(fontFamily = readerFontFamily),
    labelSmall = baseline.labelSmall.copy(fontFamily = readerFontFamily),
)
