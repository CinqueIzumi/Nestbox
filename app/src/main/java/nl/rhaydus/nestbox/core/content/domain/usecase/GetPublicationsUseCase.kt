package nl.rhaydus.nestbox.core.content.domain.usecase

import nl.rhaydus.common.runCatchingLogged
import nl.rhaydus.nestbox.core.auth.domain.model.UnauthorizedException
import nl.rhaydus.nestbox.core.auth.domain.usecase.SignOutGitHubUseCase
import nl.rhaydus.nestbox.core.content.domain.model.PublicationSummary
import nl.rhaydus.nestbox.core.content.domain.repository.PublicationRepository

class GetPublicationsUseCase(
    private val publicationRepository: PublicationRepository,
    private val signOutGitHubUseCase: SignOutGitHubUseCase,
) {
    // GitHub rejecting the credential is the moment entitlement ends, so the session is dropped here
    // rather than waiting for the user to open the profile screen.
    suspend operator fun invoke(): Result<List<PublicationSummary>> =
        runCatchingLogged("Could not load the publication list") {
            publicationRepository.getPublications()
        }.onFailure { error -> if (error is UnauthorizedException) signOutGitHubUseCase() }
}
