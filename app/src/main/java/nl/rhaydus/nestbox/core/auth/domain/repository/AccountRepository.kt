package nl.rhaydus.nestbox.core.auth.domain.repository

import nl.rhaydus.nestbox.core.auth.domain.model.AuthorizationResult
import nl.rhaydus.nestbox.core.auth.domain.model.DeviceAuthorization
import nl.rhaydus.nestbox.core.auth.domain.model.GitHubAccount

interface AccountRepository {
    suspend fun startDeviceAuthorization(): DeviceAuthorization

    suspend fun checkAuthorization(deviceCode: String): AuthorizationResult

    suspend fun getAccount(): GitHubAccount?

    suspend fun signOut()
}
