package nl.rhaydus.nestbox.core.feature.profile

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen

object ProfileScreen : Screen {
    @Composable
    override fun Content() {
        Text(text = "Profile screen")
    }
}
