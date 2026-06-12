package nl.rhaydus.nestbox.core.presentation.widget

import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalFloatingToolbar
import androidx.compose.material3.Icon
import androidx.compose.material3.ToggleButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.Tab
import nl.rhaydus.nestbox.feature.home.screen.HomeTab
import nl.rhaydus.nestbox.feature.profile.ProfileTab

private val bottomBarScreens = listOf(
    HomeTab,
    ProfileTab,
)

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun BottomFloatingBar(modifier: Modifier = Modifier) {
    val screens = remember { bottomBarScreens }

    HorizontalFloatingToolbar(
        expanded = true,
        modifier = modifier,
    ) {
        val tabNavigator = LocalTabNavigator.current

        screens.forEach { tab: Tab ->
            val isSelected = tabNavigator.current == tab
            val iconPainter = tab.options.icon ?: return@forEach

            ToggleButton(
                checked = isSelected,
                onCheckedChange = { tabNavigator.current = tab },
            ) {
                Icon(
                    painter = iconPainter,
                    contentDescription = "${tab.options.title} icon",
                )
            }
        }
    }
}
