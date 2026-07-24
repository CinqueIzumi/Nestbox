package nl.rhaydus.nestbox.core.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * The editorial tokens the Material role model cannot name honestly (design system §A1): fixed
 * values pinned to one specific call site rather than a role reused across the system. Provided the
 * same [staticCompositionLocalOf] plus `MaterialTheme` extension pattern as [ReaderTypography], with
 * distinct light and dusk values (dusk values are this file's own derivation — the redline sheet's
 * §E1 remap table only covers the nine primary tokens, not these).
 */
@Immutable
data class NestboxColors(
    /** The unselected type-facet chip's stroke (design system §B2). */
    val idleChipStroke: Color,

    /** The unselected type-facet chip's label (design system §B2). */
    val chipIdleText: Color,

    /** A ledger row's title once it has been read, demoted from the ink title colour (§B3). */
    val readRowTitle: Color,

    /** The reader note's left rule and the reader chrome circle buttons' stroke (§C, §D1). */
    val noteRule: Color,

    /** The end-of-list / footer caption, the quietest text tone in the system (§B, §D2). */
    val listFooterMuted: Color,

    /**
     * The fenced code block's fill. Equal to ink in light mode, but dusk wants it darker than the
     * canvas rather than the light-mode ink value (§E1 dusk rules), so it cannot reuse
     * `inverseSurface`, which deliberately stays fixed at ink for the ink-card family.
     */
    val codeSurface: Color,
)

val LightNestboxColors: NestboxColors = NestboxColors(
    idleChipStroke = Color(0xFFC9BFA6),
    chipIdleText = Color(0xFF6B655A),
    readRowTitle = Color(0xFF8A8272),
    noteRule = Color(0xFFD9D0BB),
    listFooterMuted = Color(0xFFB3AA93),
    codeSurface = Color(0xFF23201A),
)

val DuskNestboxColors: NestboxColors = NestboxColors(
    idleChipStroke = Color(0xFF4A4432),
    chipIdleText = Color(0xFFA69C82),
    readRowTitle = Color(0xFF6F695C),
    noteRule = Color(0xFF3D3728),
    listFooterMuted = Color(0xFF726A56),
    codeSurface = Color(0xFF12100C),
)

internal val LocalNestboxColors = staticCompositionLocalOf { LightNestboxColors }

val MaterialTheme.nestboxColors: NestboxColors
    @Composable
    @ReadOnlyComposable
    get() = LocalNestboxColors.current
