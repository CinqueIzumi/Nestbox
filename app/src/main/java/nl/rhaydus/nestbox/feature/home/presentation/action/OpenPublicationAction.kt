package nl.rhaydus.nestbox.feature.home.presentation.action

import nl.rhaydus.nestbox.core.presentation.toad.ActionScope
import nl.rhaydus.nestbox.feature.home.presentation.event.HomeEvent
import nl.rhaydus.nestbox.feature.home.presentation.screenmodel.HomeDependencies
import nl.rhaydus.nestbox.feature.home.presentation.state.HomeLocalVariables
import nl.rhaydus.nestbox.feature.home.presentation.state.HomeUiState

data class OpenPublicationAction(val publicationId: String) : HomeAction {
    override suspend fun execute(
        dependencies: HomeDependencies,
        scope: ActionScope<HomeUiState, HomeEvent, HomeLocalVariables>,
    ) {
        scope.sendEvent(HomeEvent.OpenPublicationEvent(publicationId))
    }
}
