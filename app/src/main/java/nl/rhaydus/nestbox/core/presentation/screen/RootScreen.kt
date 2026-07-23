package nl.rhaydus.nestbox.core.presentation.screen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.NavigatorDisposeBehavior

object RootScreen : Screen {
    @Composable
    override fun Content() {
        Scaffold {
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it),
            ) {
                // Pushing a screen (e.g. PublicationDetailScreen) takes BottomBarScreen out of
                // composition. Keeping the nested tab navigator alive across that push preserves
                // each tab's saved state, notably the home archive's scroll position.
                Navigator(
                    screen = BottomBarScreen,
                    disposeBehavior = NavigatorDisposeBehavior(disposeNestedNavigators = false),
                )
            }
        }
    }
}
