package nl.rhaydus.nestbox.core.auth.domain.usecase

import nl.rhaydus.nestbox.core.auth.domain.model.AuthorizationResult
import nl.rhaydus.nestbox.core.auth.domain.repository.AccountRepository
import nl.rhaydus.nestbox.core.common.runCatchingCancellable

class CheckGitHubAuthorizationUseCase(private val accountRepository: AccountRepository) {
    suspend operator fun invoke(deviceCode: String): Result<AuthorizationResult> =
        runCatchingCancellable { accountRepository.checkAuthorization(deviceCode) }
}
