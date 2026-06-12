package nl.rhaydus.nestbox.core.auth.domain.model

sealed interface AuthorizationResult {

    data class Authorized(val account: GitHubAccount) : AuthorizationResult

    data object Pending : AuthorizationResult

    data object Expired : AuthorizationResult

    data object Denied : AuthorizationResult

    data object Failed : AuthorizationResult
}
