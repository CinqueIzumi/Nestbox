package nl.rhaydus.nestbox.feature.publication.presentation.action

import nl.rhaydus.nestbox.feature.publication.presentation.event.PublicationDetailEvent
import nl.rhaydus.nestbox.feature.publication.presentation.screenmodel.PublicationDetailDependencies
import nl.rhaydus.nestbox.feature.publication.presentation.state.PublicationDetailLocalVariables
import nl.rhaydus.nestbox.feature.publication.presentation.state.PublicationDetailUiState
import nl.rhaydus.toad.ActionScope

data class OpenLinkAction(val url: String) : PublicationDetailAction {
    override suspend fun execute(
        dependencies: PublicationDetailDependencies,
        scope: ActionScope<PublicationDetailUiState, PublicationDetailEvent, PublicationDetailLocalVariables>,
    ) {
        scope.sendEvent(PublicationDetailEvent.OpenLinkEvent(url))
    }
}
