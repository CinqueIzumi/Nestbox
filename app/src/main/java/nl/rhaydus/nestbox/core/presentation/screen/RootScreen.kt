package nl.rhaydus.nestbox.core.presentation.screen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.Navigator

object RootScreen : Screen {
    @Composable
    override fun Content() {
        Scaffold() {
            Surface(
                modifier = Modifier.Companion
                    .fillMaxSize()
                    .padding(it)
            ) {
                Navigator(BottomBarScreen)
            }
        }
    }
}