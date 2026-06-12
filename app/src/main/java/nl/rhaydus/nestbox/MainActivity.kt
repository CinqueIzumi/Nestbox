package nl.rhaydus.nestbox

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalFloatingToolbar
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabNavigator
import cafe.adriel.voyager.navigator.tab.TabOptions
import nl.rhaydus.nestbox.ui.theme.NestboxTheme

val LocalBottomBarPadding = compositionLocalOf { 0.dp }

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

            ToggleButton(checked = isSelected, onCheckedChange = { tabNavigator.current = tab }) {
                Icon(painter = iconPainter, contentDescription = "${tab.options.title} icon")
            }
        }
    }
}

object HomeScreen : Screen {
    @Composable
    override fun Content() {
        Text(text = "Home screen")
    }
}

object ProfileScreen : Screen {
    @Composable
    override fun Content() {
        Text(text = "Profile screen")
    }
}

object ProfileTab : Tab {
    override val options: TabOptions
        @Composable
        get() {
            val title = "Home"
            val icon = rememberVectorPainter(Icons.Default.Person)

            return remember {
                TabOptions(
                    index = 0u,
                    title = title,
                    icon = icon,
                )
            }
        }

    @Composable
    override fun Content() = ProfileScreen.Content()
}

object HomeTab : Tab {
    override val options: TabOptions
        @Composable
        get() {
            val title = "Home"
            val icon = rememberVectorPainter(Icons.Default.Home)

            return remember {
                TabOptions(
                    index = 0u,
                    title = title,
                    icon = icon
                )
            }
        }

    @Composable
    override fun Content() = HomeScreen.Content()

}

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

object RootScreen : Screen {
    @Composable
    override fun Content() {
        Scaffold() {
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
            ) {
                Navigator(BottomBarScreen)
            }
        }
    }

}

@Composable
fun App() {
    NestboxTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            RootScreen.Content()
        }
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            App()
        }
    }
}