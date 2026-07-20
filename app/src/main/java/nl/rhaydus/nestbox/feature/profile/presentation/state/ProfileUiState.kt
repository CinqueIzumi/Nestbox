package nl.rhaydus.nestbox.feature.profile.presentation.state

import nl.rhaydus.toad.UiState

data class ProfileUiState(
    val link: GitHubLinkState = GitHubLinkState.Loading,
) : UiState
