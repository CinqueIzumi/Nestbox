package nl.rhaydus.nestbox.core.presentation.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import nl.rhaydus.nestbox.core.presentation.theme.readerTypography

/**
 * The canonical section header (design system §3.2): the 28×3 dp primary section rule, an all-caps
 * mono [kicker] in primary, the reading-sans [headline] on-surface, and an optional [description]
 * line. Sits in the page gutter to introduce a region of content.
 */
@Composable
fun SectionHeader(
    kicker: String,
    headline: String,
    modifier: Modifier = Modifier,
    description: String? = null,
) {
    Column(modifier = modifier) {
        Spacer(
            modifier = Modifier
                .size(
                    width = 28.dp,
                    height = 3.dp,
                )
                .clip(RoundedCornerShape(2.dp))
                .background(MaterialTheme.colorScheme.primary),
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = kicker.uppercase(),
            style = MaterialTheme.readerTypography.kicker,
            color = MaterialTheme.colorScheme.primary,
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = headline,
            style = MaterialTheme.readerTypography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface,
        )

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
