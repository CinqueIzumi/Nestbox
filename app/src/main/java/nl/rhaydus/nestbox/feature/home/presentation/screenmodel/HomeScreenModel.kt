package nl.rhaydus.nestbox.feature.home.presentation.screenmodel

import cafe.adriel.voyager.core.model.screenModelScope
import nl.rhaydus.nestbox.core.presentation.dispatchers.AppDispatchers
import nl.rhaydus.nestbox.core.presentation.toad.ToadScreenModel
import nl.rhaydus.nestbox.feature.home.presentation.action.HomeAction
import nl.rhaydus.nestbox.feature.home.presentation.collector.HomeCollector
import nl.rhaydus.nestbox.feature.home.presentation.event.HomeEvent
import nl.rhaydus.nestbox.feature.home.presentation.state.HomeLocalVariables
import nl.rhaydus.nestbox.feature.home.presentation.state.HomeUiState

class HomeScreenModel(
    private val appDispatchers: AppDispatchers,
    flows: List<HomeCollector>,
) : ToadScreenModel<HomeUiState, HomeEvent, HomeDependencies, HomeCollector, HomeLocalVariables>(
    initialState = HomeUiState(),
    initialLocalVariables = HomeLocalVariables(),
    initializers = flows,
) {
    override val dependencies: HomeDependencies = HomeDependencies(
        mainDispatcher = appDispatchers.main,
        coroutineScope = screenModelScope,
    )

    init {
        startInitializers()
    }

    fun runAction(action: HomeAction) = dispatch(action = action)
}