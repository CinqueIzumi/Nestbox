package nl.rhaydus.nestbox.core.auth.domain.model

/**
 * GitHub rejected the stored credential: the token was revoked, expired, or the organisation stopped
 * honouring it. Distinct from a transport failure, because it is the one error that means the
 * subscription can no longer be read and the synced content has to go.
 */
class UnauthorizedException(message: String) : Exception(message)
