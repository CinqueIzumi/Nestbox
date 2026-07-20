package nl.rhaydus.nestbox.feature.profile.presentation.screenmodel

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import nl.rhaydus.nestbox.core.auth.domain.usecase.CheckGitHubAuthorizationUseCase
import nl.rhaydus.nestbox.core.auth.domain.usecase.GetGitHubAccountUseCase
import nl.rhaydus.nestbox.core.auth.domain.usecase.SignOutGitHubUseCase
import nl.rhaydus.nestbox.core.auth.domain.usecase.StartGitHubAuthorizationUseCase
import nl.rhaydus.toad.ActionDependencies

internal class ProfileDependencies(
    override val coroutineScope: CoroutineScope,
    override val mainDispatcher: CoroutineDispatcher,
    val startGitHubAuthorizationUseCase: StartGitHubAuthorizationUseCase,
    val checkGitHubAuthorizationUseCase: CheckGitHubAuthorizationUseCase,
    val getGitHubAccountUseCase: GetGitHubAccountUseCase,
    val signOutGitHubUseCase: SignOutGitHubUseCase,
) : ActionDependencies()
