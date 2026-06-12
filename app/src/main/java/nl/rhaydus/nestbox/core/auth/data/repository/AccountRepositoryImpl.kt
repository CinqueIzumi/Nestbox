package nl.rhaydus.nestbox.core.auth.data.repository

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import nl.rhaydus.nestbox.core.auth.data.datasource.GitHubAuthRemoteDataSource
import nl.rhaydus.nestbox.core.auth.data.datasource.TokenLocalDataSource
import nl.rhaydus.nestbox.core.auth.domain.model.AuthorizationResult
import nl.rhaydus.nestbox.core.auth.domain.model.DeviceAuthorization
import nl.rhaydus.nestbox.core.auth.domain.model.GitHubAccount
import nl.rhaydus.nestbox.core.auth.domain.repository.AccountRepository

class AccountRepositoryImpl(
    private val remoteDataSource: GitHubAuthRemoteDataSource,
    private val tokenLocalDataSource: TokenLocalDataSource,
    private val clientId: String,
    private val ioDispatcher: CoroutineDispatcher,
) : AccountRepository {

    override suspend fun startDeviceAuthorization(): DeviceAuthorization = withContext(ioDispatcher) {
        val response = remoteDataSource.requestDeviceCode(clientId, SCOPE)
        DeviceAuthorization(
            userCode = response.userCode,
            verificationUri = response.verificationUri,
            deviceCode = response.deviceCode,
        )
    }

    override suspend fun checkAuthorization(deviceCode: String): AuthorizationResult =
        withContext(ioDispatcher) {
            val token = remoteDataSource.requestAccessToken(clientId, deviceCode)

            token.accessToken?.let { accessToken ->
                tokenLocalDataSource.saveToken(accessToken)
                // The token is valid and saved; a failed profile fetch must not drop the link, so
                // fall back to a placeholder account that the next restore will re-resolve.
                val account = runCatching { fetchAccount(accessToken) }
                    .getOrDefault(GitHubAccount(login = "GitHub", avatarUrl = ""))
                return@withContext AuthorizationResult.Authorized(account)
            }

            when (token.error) {
                "authorization_pending", "slow_down" -> AuthorizationResult.Pending
                "expired_token" -> AuthorizationResult.Expired
                "access_denied" -> AuthorizationResult.Denied
                else -> AuthorizationResult.Failed
            }
        }

    override suspend fun getAccount(): GitHubAccount? = withContext(ioDispatcher) {
        val token = tokenLocalDataSource.getToken() ?: return@withContext null

        fetchAccount(token)
    }

    override suspend fun signOut() = withContext(ioDispatcher) {
        tokenLocalDataSource.clear()
    }

    private suspend fun fetchAccount(token: String): GitHubAccount {
        val user = remoteDataSource.fetchUser(token)
        return GitHubAccount(login = user.login, avatarUrl = user.avatarUrl)
    }

    private companion object {
        // read:user for the profile display, repo for private repository access.
        const val SCOPE = "repo read:user"
    }
}
