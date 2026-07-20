package nl.rhaydus.nestbox.feature.profile.presentation.action

import nl.rhaydus.nestbox.feature.profile.presentation.event.ProfileEvent
import nl.rhaydus.nestbox.feature.profile.presentation.screenmodel.ProfileDependencies
import nl.rhaydus.nestbox.feature.profile.presentation.state.GitHubLinkState
import nl.rhaydus.nestbox.feature.profile.presentation.state.ProfileLocalVariables
import nl.rhaydus.nestbox.feature.profile.presentation.state.ProfileUiState
import nl.rhaydus.toad.ActionScope

data object StartGitHubLinkAction : ProfileAction {
    override suspend fun execute(
        dependencies: ProfileDependencies,
        scope: ActionScope<ProfileUiState, ProfileEvent, ProfileLocalVariables>,
    ) {
        dependencies.startGitHubAuthorizationUseCase()
            .onSuccess { authorization ->
                scope.setLocalVariables { it.copy(deviceCode = authorization.deviceCode) }
                scope.setState {
                    it.copy(
                        link = GitHubLinkState.Connecting(
                            userCode = authorization.userCode,
                            verificationUri = authorization.verificationUri,
                        ),
                    )
                }
            }
            .onFailure {
                scope.setState {
                    it.copy(link = GitHubLinkState.Disconnected(error = "Couldn't reach GitHub. Please try again."))
                }
            }
    }
}
