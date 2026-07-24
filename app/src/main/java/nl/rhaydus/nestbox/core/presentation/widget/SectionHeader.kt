package nl.rhaydus.nestbox.core.presentation.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import nl.rhaydus.nestbox.core.presentation.theme.readerTypography

/**
 * The ledger group header (design system §B3, §D2.3): a mono [kicker] label in the meta colour over
 * a full-bleed 1dp ink rule. This replaced the prior 28×3 dp primary accent bar. [headline] and
 * [description] are optional trailers kept for callers that still want a human-readable line under
 * the rule (design system §3.2's headline pairing survives as an opt-in, not the default anatomy).
 */
@Composable
fun SectionHeader(
    kicker: String,
    modifier: Modifier = Modifier,
    headline: String? = null,
    description: String? = null,
) {
    Column(modifier = modifier) {
        Text(
            text = kicker.uppercase(),
            style = MaterialTheme.readerTypography.kickerSmall,
            color = MaterialTheme.colorScheme.outline,
        )

        Spacer(modifier = Modifier.height(8.dp))

        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(MaterialTheme.colorScheme.onSurface),
        )

        if (headline != null) {
            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = headline,
                style = MaterialTheme.readerTypography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }

        if (description != null) {
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = description,
                style = MaterialTheme.readerTypography.body,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
