package nl.rhaydus.nestbox.feature.publication.presentation.collector

import nl.rhaydus.nestbox.core.presentation.toad.Collector
import nl.rhaydus.nestbox.feature.publication.presentation.event.PublicationDetailEvent
import nl.rhaydus.nestbox.feature.publication.presentation.screenmodel.PublicationDetailDependencies
import nl.rhaydus.nestbox.feature.publication.presentation.state.PublicationDetailLocalVariables
import nl.rhaydus.nestbox.feature.publication.presentation.state.PublicationDetailUiState

sealed interface PublicationDetailCollector : Collector<
        PublicationDetailUiState,
        PublicationDetailEvent,
        PublicationDetailDependencies,
        PublicationDetailLocalVariables,
        >
