package nl.rhaydus.nestbox.core.auth.domain.usecase

import nl.rhaydus.common.runCatchingLogged
import nl.rhaydus.nestbox.core.auth.domain.repository.AccountRepository
import nl.rhaydus.nestbox.core.content.domain.repository.PublicationRepository

/**
 * The single de-authentication path: drops the token and the synced publications together.
 *
 * The content is a paid subscription that exists nowhere outside the private repository, so it must
 * not outlive the credential entitled to read it. Every route that invalidates the token goes
 * through here rather than clearing storage piecemeal, so no route can forget the content half.
 */
class SignOutGitHubUseCase(
    private val accountRepository: AccountRepository,
    private val publicationRepository: PublicationRepository,
) {
    suspend operator fun invoke(): Result<Unit> = runCatchingLogged("Could not sign out of GitHub") {
        // Content first: failing to drop the token afterwards still leaves the subscription
        // unreadable, while the reverse would strand publications on disk with no entitlement left.
        publicationRepository.clearLocalContent()
        accountRepository.signOut()
    }
}
