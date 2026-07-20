package nl.rhaydus.nestbox.core.auth.domain.usecase

import nl.rhaydus.common.runCatchingCancellable
import nl.rhaydus.nestbox.core.auth.domain.model.GitHubAccount
import nl.rhaydus.nestbox.core.auth.domain.repository.AccountRepository

class GetGitHubAccountUseCase(private val accountRepository: AccountRepository) {
    // A token that can no longer resolve an account is treated as unusable and dropped, so the next
    // launch starts clean. Sign-out is best-effort and never masks the original failure.
    suspend operator fun invoke(): Result<GitHubAccount?> =
        runCatchingCancellable { accountRepository.getAccount() }
            .onFailure { runCatchingCancellable { accountRepository.signOut() } }
}
