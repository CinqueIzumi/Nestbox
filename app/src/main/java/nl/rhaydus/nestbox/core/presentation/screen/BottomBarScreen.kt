package nl.rhaydus.nestbox.core.presentation.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.TabNavigator
import nl.rhaydus.nestbox.core.presentation.util.LocalBottomBarPadding
import nl.rhaydus.nestbox.core.presentation.widget.BottomFloatingBar
import nl.rhaydus.nestbox.feature.home.screen.HomeTab

object BottomBarScreen : Screen {
    @Composable
    override fun Content() {
        BottomBarScreen()
    }

    @Composable
    private fun BottomBarScreen() {
        var bottomBarHeight by remember { mutableStateOf(0.dp) }
        val localDensity = LocalDensity.current

        val shieldInteractionSource = remember { MutableInteractionSource() }

        val bottomBarPadding = bottomBarHeight + 16.dp + WindowInsets.navigationBars
            .asPaddingValues()
            .calculateBottomPadding()

        TabNavigator(HomeTab) {
            Box(modifier = Modifier.fillMaxSize()) {
                CompositionLocalProvider(
                    LocalBottomBarPadding provides bottomBarPadding,
                ) {
                    CurrentTab()
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .windowInsetsPadding(WindowInsets.navigationBars)
                        .onSizeChanged {
                            bottomBarHeight = with(localDensity) { it.height.toDp() }
                        }
                        .clickable(
                            interactionSource = shieldInteractionSource,
                            indication = null,
                            onClick = {},
                        ),
                ) {
                    BottomFloatingBar(
                        modifier = Modifier.padding(
                            horizontal = 8.dp,
                            vertical = 6.dp,
                        ),
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}