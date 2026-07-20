package nl.rhaydus.nestbox.feature.publication.presentation.action

import nl.rhaydus.nestbox.feature.publication.presentation.event.PublicationDetailEvent
import nl.rhaydus.nestbox.feature.publication.presentation.screenmodel.PublicationDetailDependencies
import nl.rhaydus.nestbox.feature.publication.presentation.state.PublicationDetailLocalVariables
import nl.rhaydus.nestbox.feature.publication.presentation.state.PublicationDetailUiState
import nl.rhaydus.toad.UiAction

internal sealed interface PublicationDetailAction : UiAction<
        PublicationDetailDependencies,
        PublicationDetailUiState,
        PublicationDetailEvent,
        PublicationDetailLocalVariables,
        >
