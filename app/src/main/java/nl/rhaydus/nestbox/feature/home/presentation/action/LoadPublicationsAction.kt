package nl.rhaydus.nestbox.feature.home.presentation.action

import nl.rhaydus.nestbox.feature.home.presentation.event.HomeEvent
import nl.rhaydus.nestbox.feature.home.presentation.screenmodel.HomeDependencies
import nl.rhaydus.nestbox.feature.home.presentation.state.HomeLocalVariables
import nl.rhaydus.nestbox.feature.home.presentation.state.HomeUiState
import nl.rhaydus.toad.ActionScope

data object LoadPublicationsAction : HomeAction {
    override suspend fun execute(
        dependencies: HomeDependencies,
        scope: ActionScope<HomeUiState, HomeEvent, HomeLocalVariables>,
    ) {
        scope.setState { it.copy(isLoading = true, errorMessage = null) }

        dependencies.getPublicationsUseCase()
            .onSuccess { publications ->
                scope.setState {
                    it.copy(
                        isLoading = false,
                        publications = publications,
                        errorMessage = null,
                    )
                }
            }
            .onFailure {
                scope.setState {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Couldn't load the Doveletter. Pull down to try again.",
                    )
                }
            }
    }
}
