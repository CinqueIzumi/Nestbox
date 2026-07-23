package nl.rhaydus.nestbox.core.auth.data.datasource

import nl.rhaydus.common.AppLog

/**
 * Debug-only decorator that plants a personal access token from `local.properties` into the real
 * store the first time a token is asked for and none is saved yet.
 *
 * It exists because the `doveletter` organisation disallows OAuth apps, so the device flow cannot
 * mint a usable token and the token-entry UI does not exist yet. Seeding on read rather than at
 * startup keeps it race-free: whoever reads the token first triggers the write and waits for it.
 *
 * The seed fires at most once per process, so [clear] (signing out) still empties the store for the
 * rest of the session instead of being silently undone by the next read.
 */
internal class DebugSeedingTokenLocalDataSource(
    private val delegate: TokenLocalDataSource,
    private val seedToken: String,
) : TokenLocalDataSource {
    private var hasSeeded = false

    override suspend fun saveToken(token: String) = delegate.saveToken(token)

    override suspend fun getToken(): String? {
        val stored = delegate.getToken()

        if (stored != null) return stored

        if (hasSeeded) return null

        hasSeeded = true
        AppLog.i("Seeding the debug personal access token from local.properties.")
        delegate.saveToken(seedToken)

        return seedToken
    }

    override suspend fun clear() = delegate.clear()
}
