package nl.rhaydus.nestbox.feature.profile.presentation.state

import nl.rhaydus.toad.UiState

internal data class ProfileUiState(
    val link: GitHubLinkState = GitHubLinkState.Loading,
) : UiState
