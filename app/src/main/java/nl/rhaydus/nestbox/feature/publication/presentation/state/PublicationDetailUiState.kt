package nl.rhaydus.nestbox.feature.publication.presentation.state

import nl.rhaydus.nestbox.core.content.domain.model.PublicationType
import nl.rhaydus.nestbox.core.presentation.markdown.model.MarkdownBlock
import nl.rhaydus.nestbox.core.presentation.toad.UiState

data class PublicationDetailUiState(
    val isLoading: Boolean = true,
    val type: PublicationType? = null,
    val number: Int? = null,
    val date: String? = null,
    val blocks: List<MarkdownBlock> = emptyList(),
    val errorMessage: String? = null,
) : UiState
