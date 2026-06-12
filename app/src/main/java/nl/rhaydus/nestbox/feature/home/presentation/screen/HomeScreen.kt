package nl.rhaydus.nestbox.feature.home.presentation.screen

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import nl.rhaydus.nestbox.feature.home.presentation.action.HomeAction
import nl.rhaydus.nestbox.feature.home.presentation.screenmodel.HomeScreenModel
import nl.rhaydus.nestbox.feature.home.presentation.state.HomeUiState

object HomeScreen : Screen {
    @Composable
    override fun Content() {
        val screenModel = koinScreenModel<HomeScreenModel>()
        val state by screenModel.state.collectAsState()

        HomeScreen(
            state = state,
            runAction = screenModel::runAction
        )
    }

    @Composable
    fun HomeScreen(
        state: HomeUiState,
        runAction: (HomeAction) -> Unit,
    ) {
        Text(text = "Home screen: ${state.placeholder}")
    }
}
