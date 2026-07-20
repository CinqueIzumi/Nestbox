package nl.rhaydus.nestbox.feature.home.presentation.action

import nl.rhaydus.nestbox.feature.home.presentation.event.HomeEvent
import nl.rhaydus.nestbox.feature.home.presentation.screenmodel.HomeDependencies
import nl.rhaydus.nestbox.feature.home.presentation.state.HomeLocalVariables
import nl.rhaydus.nestbox.feature.home.presentation.state.HomeUiState
import nl.rhaydus.toad.UiAction

internal sealed interface HomeAction : UiAction<
        HomeDependencies,
        HomeUiState,
        HomeEvent,
        HomeLocalVariables,
        >

