package nl.rhaydus.nestbox.feature.home.presentation.state

import nl.rhaydus.nestbox.core.content.domain.model.PublicationSummary
import nl.rhaydus.toad.UiState

data class HomeUiState(
    val isLoading: Boolean = true,
    val publications: List<PublicationSummary> = emptyList(),
    val errorMessage: String? = null,
) : UiState
