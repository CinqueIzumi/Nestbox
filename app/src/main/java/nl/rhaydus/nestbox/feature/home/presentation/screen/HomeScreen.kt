package nl.rhaydus.nestbox.feature.home.presentation.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import nl.rhaydus.nestbox.core.presentation.theme.readerTypography
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
            runAction = screenModel::runAction,
        )
    }

    @Composable
    fun HomeScreen(
        state: HomeUiState,
        runAction: (HomeAction) -> Unit,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
        ) {
            Text(
                text = "THE DOVELETTER",
                style = MaterialTheme.readerTypography.kicker,
                color = MaterialTheme.colorScheme.primary,
            )

            Text(
                text = "Home",
                style = MaterialTheme.readerTypography.pageTitle,
                color = MaterialTheme.colorScheme.onSurface,
            )

            Text(
                text = state.placeholder,
                style = MaterialTheme.readerTypography.meta,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
