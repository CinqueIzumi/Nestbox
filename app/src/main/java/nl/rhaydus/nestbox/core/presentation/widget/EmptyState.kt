package nl.rhaydus.nestbox.core.presentation.widget

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import nl.rhaydus.nestbox.core.presentation.theme.readerTypography

/**
 * The canonical empty state (design system §4): an oversized, low-alpha outline [icon] over an
 * italic encouragement [headline] in the reader `pullQuote` voice (Instrument Serif italic). A
 * page-level moment for a genuinely empty root surface, not the tool for a narrow, single-facet
 * miss, which §4 deliberately keeps as a bare text line so a full glyph does not overstate a
 * momentary gap.
 *
 * The glyph sits directly above a headline that already carries the same message in words, so it is
 * treated as decorative and carries no content description: a screen reader announces the headline
 * once rather than the icon and the headline back to back.
 */
@Composable
fun EmptyState(
    icon: ImageVector,
    headline: String,
    modifier: Modifier = Modifier,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 48.dp),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.outline.copy(alpha = 0.35f),
            modifier = Modifier.size(96.dp),
        )

        Text(
            text = headline,
            style = MaterialTheme.readerTypography.pullQuote,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}
