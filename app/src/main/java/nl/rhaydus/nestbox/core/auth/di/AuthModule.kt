package nl.rhaydus.nestbox.core.auth.di

import nl.rhaydus.common.AppDispatchers
import nl.rhaydus.nestbox.BuildConfig
import nl.rhaydus.nestbox.core.auth.data.datasource.DebugSeedingTokenLocalDataSource
import nl.rhaydus.nestbox.core.auth.data.datasource.GitHubAuthRemoteDataSource
import nl.rhaydus.nestbox.core.auth.data.datasource.GitHubAuthRemoteDataSourceImpl
import nl.rhaydus.nestbox.core.auth.data.datasource.TokenLocalDataSource
import nl.rhaydus.nestbox.core.auth.data.datasource.TokenLocalDataSourceImpl
import nl.rhaydus.nestbox.core.auth.data.repository.AccountRepositoryImpl
import nl.rhaydus.nestbox.core.auth.domain.repository.AccountRepository
import nl.rhaydus.nestbox.core.auth.domain.usecase.CheckGitHubAuthorizationUseCase
import nl.rhaydus.nestbox.core.auth.domain.usecase.GetGitHubAccountUseCase
import nl.rhaydus.nestbox.core.auth.domain.usecase.SignOutGitHubUseCase
import nl.rhaydus.nestbox.core.auth.domain.usecase.StartGitHubAuthorizationUseCase
import nl.rhaydus.nestbox.core.network.createHttpClient
import nl.rhaydus.platform.AndroidSecureStorage
import nl.rhaydus.platform.SecureStorage
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val authModule = module {
    single { createHttpClient() }
    single<GitHubAuthRemoteDataSource> { GitHubAuthRemoteDataSourceImpl(client = get()) }
    single<SecureStorage> {
        AndroidSecureStorage(
            context = androidContext(),
            dispatchers = get(),
        )
    }
    single<TokenLocalDataSource> {
        val store = TokenLocalDataSourceImpl(secureStorage = get())

        // Debug builds with a token in local.properties read the private repository without the
        // device flow, which the doveletter organisation blocks for OAuth apps. Release builds
        // declare the field empty, so the decorator is never applied there.
        if (BuildConfig.DEBUG && BuildConfig.DOVELETTER_TOKEN.isNotBlank()) {
            DebugSeedingTokenLocalDataSource(
                delegate = store,
                seedToken = BuildConfig.DOVELETTER_TOKEN,
            )
        } else {
            store
        }
    }

    single<AccountRepository> {
        AccountRepositoryImpl(
            remoteDataSource = get(),
            tokenLocalDataSource = get(),
            clientId = BuildConfig.GITHUB_CLIENT_ID,
            ioDispatcher = get<AppDispatchers>().io,
        )
    }

    factory { StartGitHubAuthorizationUseCase(accountRepository = get()) }
    factory { CheckGitHubAuthorizationUseCase(accountRepository = get()) }
    factory {
        GetGitHubAccountUseCase(
            accountRepository = get(),
            signOutGitHubUseCase = get(),
        )
    }

    factory {
        SignOutGitHubUseCase(
            accountRepository = get(),
            publicationRepository = get(),
        )
    }
}
