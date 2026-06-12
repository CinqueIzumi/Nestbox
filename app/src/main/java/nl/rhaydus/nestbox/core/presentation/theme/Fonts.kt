package nl.rhaydus.nestbox.core.presentation.theme

import androidx.compose.ui.text.font.FontFamily

/**
 * The two families that carry the system, split by voice.
 *
 * [readerFontFamily] is the reading face: long-form prose, titles, and Material component chrome.
 * [monoFontFamily] is the developer-native accent for issue labels, metadata, topics, code, and
 * stat numerals. Both resolve to system generic families today. To swap in a bundled or downloadable
 * face, change these two declarations and nothing else in the theme moves.
 */
val readerFontFamily: FontFamily = FontFamily.SansSerif

val monoFontFamily: FontFamily = FontFamily.Monospace
