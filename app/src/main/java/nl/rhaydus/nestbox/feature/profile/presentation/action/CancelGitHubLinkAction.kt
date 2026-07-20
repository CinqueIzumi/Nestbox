package nl.rhaydus.nestbox.feature.profile.presentation.action

import nl.rhaydus.nestbox.feature.profile.presentation.event.ProfileEvent
import nl.rhaydus.nestbox.feature.profile.presentation.screenmodel.ProfileDependencies
import nl.rhaydus.nestbox.feature.profile.presentation.state.GitHubLinkState
import nl.rhaydus.nestbox.feature.profile.presentation.state.ProfileLocalVariables
import nl.rhaydus.nestbox.feature.profile.presentation.state.ProfileUiState
import nl.rhaydus.toad.ActionScope

data object CancelGitHubLinkAction : ProfileAction {
    override suspend fun execute(
        dependencies: ProfileDependencies,
        scope: ActionScope<ProfileUiState, ProfileEvent, ProfileLocalVariables>,
    ) {
        scope.setLocalVariables { it.copy(deviceCode = null) }
        scope.setState { it.copy(link = GitHubLinkState.Disconnected()) }
    }
}
