package nl.rhaydus.nestbox.core.auth.domain.usecase

import nl.rhaydus.common.runCatchingCancellable
import nl.rhaydus.nestbox.core.auth.domain.repository.AccountRepository

class SignOutGitHubUseCase(private val accountRepository: AccountRepository) {
    suspend operator fun invoke(): Result<Unit> =
        runCatchingCancellable { accountRepository.signOut() }
}
