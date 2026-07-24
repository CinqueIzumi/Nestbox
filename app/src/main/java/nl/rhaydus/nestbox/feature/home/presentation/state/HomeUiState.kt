package nl.rhaydus.nestbox.feature.home.presentation.state

import nl.rhaydus.nestbox.core.content.domain.model.PublicationSummary
import nl.rhaydus.toad.UiState

internal data class HomeUiState(
    val isLoading: Boolean = true,
    val selectedFilter: PublicationFilter = PublicationFilter.ALL,
    val hero: PublicationSummary? = null,
    val sections: List<PublicationSection> = emptyList(),
    val errorMessage: String? = null,
) : UiState
