package nl.rhaydus.nestbox.feature.profile.di

import nl.rhaydus.nestbox.feature.profile.presentation.screenmodel.ProfileScreenModel
import org.koin.dsl.module

val profileModule = module {
    factory {
        ProfileScreenModel(
            appDispatchers = get(),
            startGitHubAuthorizationUseCase = get(),
            checkGitHubAuthorizationUseCase = get(),
            getGitHubAccountUseCase = get(),
            signOutGitHubUseCase = get(),
            flows = emptyList(),
        )
    }
}
