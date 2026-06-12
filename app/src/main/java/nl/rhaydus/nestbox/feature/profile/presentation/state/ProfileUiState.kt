package nl.rhaydus.nestbox.feature.profile.presentation.state

import nl.rhaydus.nestbox.core.presentation.toad.UiState

data class ProfileUiState(
    val placeholder: String = "",
) : UiState
