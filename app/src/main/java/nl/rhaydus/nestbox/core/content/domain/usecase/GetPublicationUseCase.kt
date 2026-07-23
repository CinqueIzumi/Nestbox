package nl.rhaydus.nestbox.core.content.domain.usecase

import nl.rhaydus.common.runCatchingLogged
import nl.rhaydus.nestbox.core.auth.domain.model.UnauthorizedException
import nl.rhaydus.nestbox.core.auth.domain.usecase.SignOutGitHubUseCase
import nl.rhaydus.nestbox.core.content.domain.model.Publication
import nl.rhaydus.nestbox.core.content.domain.repository.PublicationRepository

class GetPublicationUseCase(
    private val publicationRepository: PublicationRepository,
    private val signOutGitHubUseCase: SignOutGitHubUseCase,
) {
    suspend operator fun invoke(id: String): Result<Publication> =
        runCatchingLogged("Could not load publication '$id'") {
            publicationRepository.getPublication(id)
        }.onFailure { error -> if (error is UnauthorizedException) signOutGitHubUseCase() }
}
