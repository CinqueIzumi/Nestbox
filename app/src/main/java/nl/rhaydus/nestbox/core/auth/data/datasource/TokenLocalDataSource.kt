package nl.rhaydus.nestbox.core.auth.data.datasource

import nl.rhaydus.platform.SecureStorage

interface TokenLocalDataSource {
    suspend fun saveToken(token: String)

    suspend fun getToken(): String?

    suspend fun clear()
}

/**
 * The GitHub access token, custodied by the foundation's [SecureStorage]: the ciphertext lives in an
 * app-private file and the AES key in the Android Keystore. Discarding ciphertext that no longer
 * decrypts (a rotated or invalidated Keystore key) is [SecureStorage]'s job, so a failed read simply
 * reads as "no token" here and the user re-links.
 */
class TokenLocalDataSourceImpl(
    private val secureStorage: SecureStorage,
) : TokenLocalDataSource {

    override suspend fun saveToken(token: String) = secureStorage.write(
        key = TOKEN_KEY,
        value = token,
    )

    override suspend fun getToken(): String? = secureStorage.read(TOKEN_KEY)

    override suspend fun clear() = secureStorage.delete(TOKEN_KEY)

    private companion object {
        const val TOKEN_KEY = "github_token"
    }
}
