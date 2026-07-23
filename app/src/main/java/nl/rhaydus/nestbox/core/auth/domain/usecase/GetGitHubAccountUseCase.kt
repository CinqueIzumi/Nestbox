package nl.rhaydus.nestbox.core.auth.domain.usecase

import nl.rhaydus.common.runCatchingCancellable
import nl.rhaydus.nestbox.core.auth.domain.model.GitHubAccount
import nl.rhaydus.nestbox.core.auth.domain.repository.AccountRepository

class GetGitHubAccountUseCase(
    private val accountRepository: AccountRepository,
    private val signOutGitHubUseCase: SignOutGitHubUseCase,
) {
    // A token that can no longer resolve an account is treated as unusable, so the whole session
    // goes (token and synced publications alike) and the next launch starts clean. Sign-out already
    // returns a Result, so it is best-effort here and never masks the original failure.
    suspend operator fun invoke(): Result<GitHubAccount?> =
        runCatchingCancellable { accountRepository.getAccount() }
            .onFailure { signOutGitHubUseCase() }
}
