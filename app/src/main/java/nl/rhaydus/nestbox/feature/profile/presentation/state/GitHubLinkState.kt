package nl.rhaydus.nestbox.feature.profile.presentation.state

import nl.rhaydus.nestbox.core.auth.domain.model.GitHubAccount

sealed interface GitHubLinkState {

    data object Loading : GitHubLinkState

    data class Disconnected(val error: String? = null) : GitHubLinkState

    data class Connecting(
        val userCode: String,
        val verificationUri: String,
        val isChecking: Boolean = false,
    ) : GitHubLinkState

    data class Connected(val account: GitHubAccount) : GitHubLinkState
}
