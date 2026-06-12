package nl.rhaydus.nestbox.core.auth.data.datasource

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import nl.rhaydus.nestbox.core.auth.data.security.CryptoManager

private val Context.authDataStore by preferencesDataStore(name = "github_auth")

interface TokenLocalDataSource {
    suspend fun saveToken(token: String)

    suspend fun getToken(): String?

    suspend fun clear()
}

class TokenLocalDataSourceImpl(
    private val context: Context,
    private val crypto: CryptoManager,
) : TokenLocalDataSource {
    private val tokenKey = stringPreferencesKey("github_token")

    override suspend fun saveToken(token: String) {
        val encrypted = crypto.encrypt(token)
        context.authDataStore.edit { prefs -> prefs[tokenKey] = encrypted }
    }

    override suspend fun getToken(): String? {
        val stored = context.authDataStore.data.first()[tokenKey] ?: return null
        return runCatching { crypto.decrypt(stored) }.getOrNull()
    }

    override suspend fun clear() {
        context.authDataStore.edit { prefs -> prefs.remove(tokenKey) }
    }
}
