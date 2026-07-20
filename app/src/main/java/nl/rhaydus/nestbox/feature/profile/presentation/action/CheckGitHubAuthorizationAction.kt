package nl.rhaydus.nestbox.feature.profile.presentation.action

import nl.rhaydus.nestbox.core.auth.domain.model.AuthorizationResult
import nl.rhaydus.nestbox.feature.profile.presentation.event.ProfileEvent
import nl.rhaydus.nestbox.feature.profile.presentation.screenmodel.ProfileDependencies
import nl.rhaydus.nestbox.feature.profile.presentation.state.GitHubLinkState
import nl.rhaydus.nestbox.feature.profile.presentation.state.ProfileLocalVariables
import nl.rhaydus.nestbox.feature.profile.presentation.state.ProfileUiState
import nl.rhaydus.toad.ActionScope

// Dispatched on every screen ON_RESUME. While linking it polls GitHub once (the user has just
// returned from approving in the browser); on first show it restores any previously stored link.
internal data object CheckGitHubAuthorizationAction : ProfileAction {
    override suspend fun execute(
        dependencies: ProfileDependencies,
        scope: ActionScope<ProfileUiState, ProfileEvent, ProfileLocalVariables>,
    ) {
        val link = scope.currentState.link
        val deviceCode = scope.currentLocalVariables.deviceCode

        if (link is GitHubLinkState.Connecting && deviceCode != null) {
            scope.setState { it.copy(link = link.copy(isChecking = true)) }

            dependencies.checkGitHubAuthorizationUseCase(deviceCode)
                .onSuccess { result ->
                    when (result) {
                        is AuthorizationResult.Authorized -> {
                            scope.setLocalVariables { it.copy(deviceCode = null) }
                            scope.setState { it.copy(link = GitHubLinkState.Connected(result.account)) }
                        }

                        // Still waiting, or an unknown GitHub verdict, keep showing the code, the
                        // user can return again. Only a definitive verdict ends the attempt.
                        AuthorizationResult.Pending,
                        AuthorizationResult.Failed -> scope.setState { it.copy(link = link.copy(isChecking = false)) }

                        AuthorizationResult.Expired -> {
                            scope.setLocalVariables { it.copy(deviceCode = null) }
                            scope.setState { it.copy(link = GitHubLinkState.Disconnected(error = "The code expired. Please try again.")) }
                        }

                        AuthorizationResult.Denied -> {
                            scope.setLocalVariables { it.copy(deviceCode = null) }
                            scope.setState { it.copy(link = GitHubLinkState.Disconnected(error = "Authorization was cancelled.")) }
                        }
                    }
                }
                .onFailure {
                    // A thrown failure here is a transient transport error while polling, keep
                    // showing the code so the user can return and we retry on the next resume.
                    scope.setState { it.copy(link = link.copy(isChecking = false)) }
                }
            return
        }

        if (link is GitHubLinkState.Loading) {
            dependencies.getGitHubAccountUseCase()
                .onSuccess { account ->
                    scope.setState {
                        it.copy(
                            link = account?.let { resolved -> GitHubLinkState.Connected(resolved) }
                                ?: GitHubLinkState.Disconnected(),
                        )
                    }
                }
                .onFailure {
                    scope.setState { it.copy(link = GitHubLinkState.Disconnected()) }
                }
        }
    }
}
