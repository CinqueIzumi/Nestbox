package nl.rhaydus.nestbox.core.auth.domain.usecase

import nl.rhaydus.nestbox.core.auth.domain.repository.AccountRepository
import nl.rhaydus.nestbox.core.common.runCatchingCancellable

class SignOutGitHubUseCase(private val accountRepository: AccountRepository) {
    suspend operator fun invoke(): Result<Unit> =
        runCatchingCancellable { accountRepository.signOut() }
}
