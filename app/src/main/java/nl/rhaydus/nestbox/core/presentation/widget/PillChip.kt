package nl.rhaydus.nestbox.core.presentation.widget

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import nl.rhaydus.nestbox.core.presentation.theme.nestboxColors
import nl.rhaydus.nestbox.core.presentation.theme.readerTypography

private val pillShape = RoundedCornerShape(percent = 50)

/**
 * The type-facet pill (design system §B2): h30, padded 7×13, a mono label at 10sp with 0.14em
 * tracking. Selected fills ink with paper text; idle stays transparent with a
 * [NestboxColors.idleChipStroke][nl.rhaydus.nestbox.core.presentation.theme.NestboxColors]-stroked
 * outline and an `idleChipText`-coloured label. Pass [onClick] for an interactive facet chip; omit it
 * for a read-only tag, which then carries no click semantics and no ripple.
 */
@Composable
fun PillChip(
    label: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
) {
    val ink = MaterialTheme.colorScheme.inverseSurface
    val paper = MaterialTheme.colorScheme.inverseOnSurface

    val containerColor = if (isSelected) ink else Color.Transparent
    val contentColor = if (isSelected) paper else MaterialTheme.nestboxColors.chipIdleText
    val border = if (isSelected) null else BorderStroke(
        1.dp,
        MaterialTheme.nestboxColors.idleChipStroke,
    )

    val labelContent = @Composable {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .height(30.dp)
                .padding(horizontal = 13.dp),
        ) {
            Text(
                text = label,
                style = MaterialTheme.readerTypography.chipLabel,
            )
        }
    }

    if (onClick != null) {
        Surface(
            onClick = onClick,
            shape = pillShape,
            color = containerColor,
            contentColor = contentColor,
            border = border,
            modifier = modifier,
            content = labelContent,
        )
    } else {
        Surface(
            shape = pillShape,
            color = containerColor,
            contentColor = contentColor,
            border = border,
            modifier = modifier,
            content = labelContent,
        )
    }
}
