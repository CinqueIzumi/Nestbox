package nl.rhaydus.nestbox.core.presentation.widget

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.Tab
import nl.rhaydus.nestbox.core.presentation.theme.readerTypography
import nl.rhaydus.nestbox.feature.home.presentation.screen.HomeTab
import nl.rhaydus.nestbox.feature.profile.presentation.screen.ProfileTab

private val bottomBarScreens = listOf(
    HomeTab,
    ProfileTab,
)

private val pillShape = RoundedCornerShape(percent = 50)

/**
 * The floating text-label tab bar (design system §D1): a translucent ink pill carrying the root tab
 * labels, `NEST` and `PROFILE`, in place of icons. This is the one component in the system that
 * keeps a drop shadow (§Shape & elevation names it the sanctioned brand exception); everywhere else
 * elevation is tonal. It reads ink in both light and dusk (§E1, "toolbar unchanged"), which is why it
 * pulls its ink/paper pair from `inverseSurface`/`inverseOnSurface` rather than `primary`/`onSurface`
 * — those two stay fixed at the same value across both color schemes.
 *
 * Backdrop blur behind the pill is not reproduced: the 92%-opacity ink fill plus the shadow carries
 * the "floating" read without a platform-version-gated blur effect. The spec's hide-on-scroll-down /
 * reveal-on-scroll-up behaviour needs a scroll signal from whichever tab is current, which isn't
 * wired up by this render-layer pass; the bar stays always-visible until a screen agent plumbs it.
 */
@Composable
fun BottomFloatingBar(modifier: Modifier = Modifier) {
    val screens = remember { bottomBarScreens }
    val tabNavigator = LocalTabNavigator.current
    val ink = MaterialTheme.colorScheme.inverseSurface
    val paper = MaterialTheme.colorScheme.inverseOnSurface

    Surface(
        color = ink.copy(alpha = 0.92f),
        contentColor = paper,
        shape = pillShape,
        shadowElevation = 12.dp,
        modifier = modifier,
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            modifier = Modifier.padding(5.dp),
        ) {
            screens.forEach { tab: Tab ->
                val isSelected = tabNavigator.current == tab

                BottomFloatingBarItem(
                    label = tab.options.title,
                    isSelected = isSelected,
                    paper = paper,
                    onClick = { tabNavigator.current = tab },
                )
            }
        }
    }
}

@Composable
private fun BottomFloatingBarItem(
    label: String,
    isSelected: Boolean,
    paper: Color,
    onClick: () -> Unit,
) {
    val fillColor = if (isSelected) {
        paper.copy(alpha = 0.12f)
    } else {
        Color.Transparent
    }
    val contentAlpha = if (isSelected) 1f else 0.65f

    Surface(
        onClick = onClick,
        shape = pillShape,
        color = fillColor,
        contentColor = paper.copy(alpha = contentAlpha),
    ) {
        Text(
            text = label.uppercase(),
            style = MaterialTheme.readerTypography.tabLabel,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 9.dp),
        )
    }
}
