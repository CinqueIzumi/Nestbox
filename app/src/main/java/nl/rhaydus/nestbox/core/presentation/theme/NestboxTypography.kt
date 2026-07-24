package nl.rhaydus.nestbox.core.presentation.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.unit.sp

private val baseline = Typography()

/**
 * The Material type scale consumed implicitly by off-the-shelf Material 3 components such as top-bar
 * titles, button labels, and navigation labels. Display/headline/title roles set in
 * [displaySerifFontFamily] (the same voice as [ReaderTypography.headline] and its siblings), body
 * roles in [bodySerifFontFamily], and label roles — button and navigation text — in [monoFontFamily]:
 * the system's chrome, wherever it comes from a stock Material component rather than a
 * screen-composed one, speaks in the same mono voice as [ReaderTypography]'s kicker and meta roles
 * (design system §1, "monospace as voice"). The screen-composed editorial roles live in
 * [ReaderTypography]. The body roles get a touch more line height than the Material default so
 * running prose reads comfortably.
 */
val NestboxTypography: Typography = Typography(
    displayLarge = baseline.displayLarge.copy(fontFamily = displaySerifFontFamily),
    displayMedium = baseline.displayMedium.copy(fontFamily = displaySerifFontFamily),
    displaySmall = baseline.displaySmall.copy(fontFamily = displaySerifFontFamily),
    headlineLarge = baseline.headlineLarge.copy(fontFamily = displaySerifFontFamily),
    headlineMedium = baseline.headlineMedium.copy(fontFamily = displaySerifFontFamily),
    headlineSmall = baseline.headlineSmall.copy(fontFamily = displaySerifFontFamily),
    titleLarge = baseline.titleLarge.copy(fontFamily = displaySerifFontFamily),
    titleMedium = baseline.titleMedium.copy(fontFamily = displaySerifFontFamily),
    titleSmall = baseline.titleSmall.copy(fontFamily = displaySerifFontFamily),
    bodyLarge = baseline.bodyLarge.copy(
        fontFamily = bodySerifFontFamily,
        lineHeight = 26.sp,
    ),
    bodyMedium = baseline.bodyMedium.copy(fontFamily = bodySerifFontFamily),
    bodySmall = baseline.bodySmall.copy(fontFamily = bodySerifFontFamily),
    labelLarge = baseline.labelLarge.copy(fontFamily = monoFontFamily),
    labelMedium = baseline.labelMedium.copy(fontFamily = monoFontFamily),
    labelSmall = baseline.labelSmall.copy(fontFamily = monoFontFamily),
)
