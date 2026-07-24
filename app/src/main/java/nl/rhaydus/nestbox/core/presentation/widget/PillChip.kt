package nl.rhaydus.nestbox.core.presentation.widget

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * The canonical fully-rounded pill (design system §4): a [Surface] carrying a single label.
 * [isSelected] swaps to `secondaryContainer`/`onSecondaryContainer` with a semibold label, idle sits
 * on `surfaceContainerHigh`/`onSurface`. Pass [onClick] for an interactive chip such as a filter
 * facet; omit it for a read-only tag.
 */
@Composable
fun PillChip(
    label: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
) {
    val containerColor = if (isSelected) {
        MaterialTheme.colorScheme.secondaryContainer
    } else {
        MaterialTheme.colorScheme.surfaceContainerHigh
    }

    val contentColor = if (isSelected) {
        MaterialTheme.colorScheme.onSecondaryContainer
    } else {
        MaterialTheme.colorScheme.onSurface
    }

    val labelStyle = if (isSelected) {
        MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold)
    } else {
        MaterialTheme.typography.labelLarge
    }

    val labelContent = @Composable {
        Text(
            text = label,
            style = labelStyle,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        )
    }

    if (onClick != null) {
        Surface(
            onClick = onClick,
            shape = RoundedCornerShape(percent = 50),
            color = containerColor,
            contentColor = contentColor,
            modifier = modifier,
            content = labelContent,
        )
    } else {
        Surface(
            shape = RoundedCornerShape(percent = 50),
            color = containerColor,
            contentColor = contentColor,
            modifier = modifier,
            content = labelContent,
        )
    }
}
