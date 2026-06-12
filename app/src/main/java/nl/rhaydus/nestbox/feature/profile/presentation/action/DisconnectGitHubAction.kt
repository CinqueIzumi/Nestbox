package nl.rhaydus.nestbox.feature.profile.presentation.action

import nl.rhaydus.nestbox.core.presentation.toad.ActionScope
import nl.rhaydus.nestbox.feature.profile.presentation.event.ProfileEvent
import nl.rhaydus.nestbox.feature.profile.presentation.screenmodel.ProfileDependencies
import nl.rhaydus.nestbox.feature.profile.presentation.state.GitHubLinkState
import nl.rhaydus.nestbox.feature.profile.presentation.state.ProfileLocalVariables
import nl.rhaydus.nestbox.feature.profile.presentation.state.ProfileUiState

data object DisconnectGitHubAction : ProfileAction {
    override suspend fun execute(
        dependencies: ProfileDependencies,
        scope: ActionScope<ProfileUiState, ProfileEvent, ProfileLocalVariables>,
    ) {
        // Sign-out is best-effort: the user is disconnected locally whether or not clearing the
        // stored token succeeded.
        dependencies.signOutGitHubUseCase()

        scope.setLocalVariables { it.copy(deviceCode = null) }
        scope.setState { it.copy(link = GitHubLinkState.Disconnected()) }
    }
}
