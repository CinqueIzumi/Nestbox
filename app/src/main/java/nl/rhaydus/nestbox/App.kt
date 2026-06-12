package nl.rhaydus.nestbox

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import nl.rhaydus.nestbox.core.presentation.screen.RootScreen
import nl.rhaydus.nestbox.core.presentation.theme.NestboxTheme

@Composable
fun App() {
    NestboxTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            RootScreen.Content()
        }
    }
}
