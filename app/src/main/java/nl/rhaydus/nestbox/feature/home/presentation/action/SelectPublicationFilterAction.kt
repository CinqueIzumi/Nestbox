package nl.rhaydus.nestbox.feature.home.presentation.action

import nl.rhaydus.nestbox.feature.home.presentation.event.HomeEvent
import nl.rhaydus.nestbox.feature.home.presentation.screenmodel.HomeDependencies
import nl.rhaydus.nestbox.feature.home.presentation.state.HomeLocalVariables
import nl.rhaydus.nestbox.feature.home.presentation.state.HomeUiState
import nl.rhaydus.nestbox.feature.home.presentation.state.PublicationFilter
import nl.rhaydus.nestbox.feature.home.presentation.state.deriveHomeSelection
import nl.rhaydus.toad.ActionScope

internal data class SelectPublicationFilterAction(val filter: PublicationFilter) : HomeAction {
    override suspend fun execute(
        dependencies: HomeDependencies,
        scope: ActionScope<HomeUiState, HomeEvent, HomeLocalVariables>,
    ) {
        val selection = deriveHomeSelection(
            publications = scope.currentLocalVariables.publications,
            filter = filter,
        )

        scope.setState {
            it.copy(
                selectedFilter = filter,
                hero = selection.hero,
                sections = selection.sections,
            )
        }
    }
}
