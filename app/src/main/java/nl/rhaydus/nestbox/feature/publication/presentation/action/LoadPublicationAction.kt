package nl.rhaydus.nestbox.feature.publication.presentation.action

import kotlinx.coroutines.withContext
import nl.rhaydus.nestbox.feature.publication.presentation.event.PublicationDetailEvent
import nl.rhaydus.nestbox.feature.publication.presentation.screenmodel.PublicationDetailDependencies
import nl.rhaydus.nestbox.feature.publication.presentation.state.PublicationDetailLocalVariables
import nl.rhaydus.nestbox.feature.publication.presentation.state.PublicationDetailUiState
import nl.rhaydus.toad.ActionScope

internal data class LoadPublicationAction(val publicationId: String) : PublicationDetailAction {
    override suspend fun execute(
        dependencies: PublicationDetailDependencies,
        scope: ActionScope<PublicationDetailUiState, PublicationDetailEvent, PublicationDetailLocalVariables>,
    ) {
        scope.setState { it.copy(
            isLoading = true,
            errorMessage = null,
        ) }

        dependencies.getPublicationUseCase(publicationId)
            .onSuccess { publication ->
                val blocks = withContext(dependencies.defaultDispatcher) {
                    dependencies.markdownParser.parse(publication.markdown)
                }

                scope.setState {
                    it.copy(
                        isLoading = false,
                        type = publication.type,
                        number = publication.number,
                        date = publication.date,
                        blocks = blocks,
                        errorMessage = null,
                    )
                }
            }
            .onFailure {
                scope.setState {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Couldn't open this publication. Please try again.",
                    )
                }
            }
    }
}
