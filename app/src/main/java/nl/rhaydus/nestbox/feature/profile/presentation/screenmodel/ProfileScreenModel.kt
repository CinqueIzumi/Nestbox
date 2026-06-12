package nl.rhaydus.nestbox.feature.profile.presentation.screenmodel

import cafe.adriel.voyager.core.model.screenModelScope
import nl.rhaydus.nestbox.core.auth.domain.usecase.CheckGitHubAuthorizationUseCase
import nl.rhaydus.nestbox.core.auth.domain.usecase.GetGitHubAccountUseCase
import nl.rhaydus.nestbox.core.auth.domain.usecase.SignOutGitHubUseCase
import nl.rhaydus.nestbox.core.auth.domain.usecase.StartGitHubAuthorizationUseCase
import nl.rhaydus.nestbox.core.presentation.dispatchers.AppDispatchers
import nl.rhaydus.nestbox.core.presentation.toad.ToadScreenModel
import nl.rhaydus.nestbox.feature.profile.presentation.action.ProfileAction
import nl.rhaydus.nestbox.feature.profile.presentation.collector.ProfileCollector
import nl.rhaydus.nestbox.feature.profile.presentation.event.ProfileEvent
import nl.rhaydus.nestbox.feature.profile.presentation.state.ProfileLocalVariables
import nl.rhaydus.nestbox.feature.profile.presentation.state.ProfileUiState

class ProfileScreenModel(
    private val appDispatchers: AppDispatchers,
    private val startGitHubAuthorizationUseCase: StartGitHubAuthorizationUseCase,
    private val checkGitHubAuthorizationUseCase: CheckGitHubAuthorizationUseCase,
    private val getGitHubAccountUseCase: GetGitHubAccountUseCase,
    private val signOutGitHubUseCase: SignOutGitHubUseCase,
    flows: List<ProfileCollector>,
) : ToadScreenModel<ProfileUiState, ProfileEvent, ProfileDependencies, ProfileCollector, ProfileLocalVariables>(
    initialState = ProfileUiState(),
    initialLocalVariables = ProfileLocalVariables(),
    initializers = flows,
) {
    override val dependencies: ProfileDependencies = ProfileDependencies(
        mainDispatcher = appDispatchers.main,
        coroutineScope = screenModelScope,
        startGitHubAuthorizationUseCase = startGitHubAuthorizationUseCase,
        checkGitHubAuthorizationUseCase = checkGitHubAuthorizationUseCase,
        getGitHubAccountUseCase = getGitHubAccountUseCase,
        signOutGitHubUseCase = signOutGitHubUseCase,
    )

    init {
        startInitializers()
    }

    fun runAction(action: ProfileAction) = dispatch(action = action)
}
