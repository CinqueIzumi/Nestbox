package nl.rhaydus.nestbox.core.auth.di

import nl.rhaydus.nestbox.BuildConfig
import nl.rhaydus.nestbox.core.auth.data.datasource.GitHubAuthRemoteDataSource
import nl.rhaydus.nestbox.core.auth.data.datasource.GitHubAuthRemoteDataSourceImpl
import nl.rhaydus.nestbox.core.auth.data.datasource.TokenLocalDataSource
import nl.rhaydus.nestbox.core.auth.data.datasource.TokenLocalDataSourceImpl
import nl.rhaydus.nestbox.core.auth.data.repository.AccountRepositoryImpl
import nl.rhaydus.nestbox.core.auth.data.security.CryptoManager
import nl.rhaydus.nestbox.core.auth.domain.repository.AccountRepository
import nl.rhaydus.nestbox.core.auth.domain.usecase.CheckGitHubAuthorizationUseCase
import nl.rhaydus.nestbox.core.auth.domain.usecase.GetGitHubAccountUseCase
import nl.rhaydus.nestbox.core.auth.domain.usecase.SignOutGitHubUseCase
import nl.rhaydus.nestbox.core.auth.domain.usecase.StartGitHubAuthorizationUseCase
import nl.rhaydus.nestbox.core.network.createHttpClient
import nl.rhaydus.nestbox.core.presentation.dispatchers.AppDispatchers
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val authModule = module {
    single { createHttpClient() }
    single<GitHubAuthRemoteDataSource> { GitHubAuthRemoteDataSourceImpl(client = get()) }
    single { CryptoManager() }
    single<TokenLocalDataSource> { TokenLocalDataSourceImpl(context = androidContext(), crypto = get()) }

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
    factory { GetGitHubAccountUseCase(accountRepository = get()) }
    factory { SignOutGitHubUseCase(accountRepository = get()) }
}
