package nl.rhaydus.nestbox.feature.publication.presentation.action

import nl.rhaydus.nestbox.core.presentation.toad.UiAction
import nl.rhaydus.nestbox.feature.publication.presentation.event.PublicationDetailEvent
import nl.rhaydus.nestbox.feature.publication.presentation.screenmodel.PublicationDetailDependencies
import nl.rhaydus.nestbox.feature.publication.presentation.state.PublicationDetailLocalVariables
import nl.rhaydus.nestbox.feature.publication.presentation.state.PublicationDetailUiState

sealed interface PublicationDetailAction : UiAction<
        PublicationDetailDependencies,
        PublicationDetailUiState,
        PublicationDetailEvent,
        PublicationDetailLocalVariables,
        >
