package nl.rhaydus.nestbox.core.presentation.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.TabNavigator
import nl.rhaydus.designsystem.layout.BottomBarScaffold
import nl.rhaydus.nestbox.core.presentation.widget.BottomFloatingBar
import nl.rhaydus.nestbox.feature.home.presentation.screen.HomeTab

object BottomBarScreen : Screen {
    @Composable
    override fun Content() {
        BottomBarScreen()
    }

    @Composable
    private fun BottomBarScreen() {
        // Swallows taps that land on the bar's own padding, so they never reach the tab behind it.
        val shieldInteractionSource = remember { MutableInteractionSource() }

        TabNavigator(HomeTab) {
            BottomBarScaffold(
                bottomBar = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable(
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
                },
            ) {
                CurrentTab()
            }
        }
    }
}
