package nl.rhaydus.nestbox.feature.publication.presentation.collector

import nl.rhaydus.nestbox.feature.publication.presentation.event.PublicationDetailEvent
import nl.rhaydus.nestbox.feature.publication.presentation.screenmodel.PublicationDetailDependencies
import nl.rhaydus.nestbox.feature.publication.presentation.state.PublicationDetailLocalVariables
import nl.rhaydus.nestbox.feature.publication.presentation.state.PublicationDetailUiState
import nl.rhaydus.toad.Collector

internal sealed interface PublicationDetailCollector : Collector<
        PublicationDetailUiState,
        PublicationDetailEvent,
        PublicationDetailDependencies,
        PublicationDetailLocalVariables,
        >
