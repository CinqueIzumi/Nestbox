package nl.rhaydus.nestbox.feature.profile.presentation.screen

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import nl.rhaydus.nestbox.feature.profile.presentation.action.ProfileAction
import nl.rhaydus.nestbox.feature.profile.presentation.screenmodel.ProfileScreenModel
import nl.rhaydus.nestbox.feature.profile.presentation.state.ProfileUiState

object ProfileScreen : Screen {
    @Composable
    override fun Content() {
        val screenModel = koinScreenModel<ProfileScreenModel>()
        val state by screenModel.state.collectAsState()

        ProfileScreen(
            state = state,
            runAction = screenModel::runAction,
        )
    }

    @Composable
    fun ProfileScreen(
        state: ProfileUiState,
        runAction: (ProfileAction) -> Unit,
    ) {
        Text(text = "Profile screen: ${state.placeholder}")
    }
}
