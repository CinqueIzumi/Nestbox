package nl.rhaydus.nestbox.core.presentation.theme

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import nl.rhaydus.nestbox.R

/**
 * The three voices that carry the system (design system §2.2): a display serif for titles and
 * headlines, a body serif for running prose, and a monospace accent for chrome — kickers, meta
 * strips, chips, and code. All three are bundled TTFs under `res/font/`, so the app no longer
 * depends on system generic families.
 *
 * [monoFontFamily] keeps its name across the redesign, only the backing face changes (`Monospace` to
 * Spline Sans Mono). The prior [readerFontFamily] (`SansSerif`) is retired: both reading roles now
 * resolve to a serif rather than a generic sans, so there is no remaining plain-sans voice.
 */
val displaySerifFontFamily: FontFamily = FontFamily(
    Font(
        resId = R.font.instrument_serif_regular,
        weight = FontWeight.Normal,
        style = FontStyle.Normal,
    ),
    Font(
        resId = R.font.instrument_serif_italic,
        weight = FontWeight.Normal,
        style = FontStyle.Italic,
    ),
)

val bodySerifFontFamily: FontFamily = FontFamily(
    Font(
        resId = R.font.newsreader_variable,
        weight = FontWeight.Normal,
        style = FontStyle.Normal,
        variationSettings = FontVariation.Settings(FontVariation.weight(400)),
    ),
    Font(
        resId = R.font.newsreader_variable,
        weight = FontWeight.Medium,
        style = FontStyle.Normal,
        variationSettings = FontVariation.Settings(FontVariation.weight(500)),
    ),
    Font(
        resId = R.font.newsreader_variable,
        weight = FontWeight.SemiBold,
        style = FontStyle.Normal,
        variationSettings = FontVariation.Settings(FontVariation.weight(600)),
    ),
    Font(
        resId = R.font.newsreader_italic_variable,
        weight = FontWeight.Normal,
        style = FontStyle.Italic,
        variationSettings = FontVariation.Settings(FontVariation.weight(400)),
    ),
)

val monoFontFamily: FontFamily = FontFamily(
    Font(
        resId = R.font.spline_sans_mono_variable,
        weight = FontWeight.Normal,
        style = FontStyle.Normal,
        variationSettings = FontVariation.Settings(FontVariation.weight(400)),
    ),
    Font(
        resId = R.font.spline_sans_mono_variable,
        weight = FontWeight.Medium,
        style = FontStyle.Normal,
        variationSettings = FontVariation.Settings(FontVariation.weight(500)),
    ),
    Font(
        resId = R.font.spline_sans_mono_variable,
        weight = FontWeight.SemiBold,
        style = FontStyle.Normal,
        variationSettings = FontVariation.Settings(FontVariation.weight(600)),
    ),
)
