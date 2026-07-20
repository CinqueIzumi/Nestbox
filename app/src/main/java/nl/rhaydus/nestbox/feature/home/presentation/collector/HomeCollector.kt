package nl.rhaydus.nestbox.feature.home.presentation.collector

import nl.rhaydus.nestbox.feature.home.presentation.event.HomeEvent
import nl.rhaydus.nestbox.feature.home.presentation.screenmodel.HomeDependencies
import nl.rhaydus.nestbox.feature.home.presentation.state.HomeLocalVariables
import nl.rhaydus.nestbox.feature.home.presentation.state.HomeUiState
import nl.rhaydus.toad.Collector

sealed interface HomeCollector : Collector<
        HomeUiState,
        HomeEvent,
        HomeDependencies,
        HomeLocalVariables,
        >