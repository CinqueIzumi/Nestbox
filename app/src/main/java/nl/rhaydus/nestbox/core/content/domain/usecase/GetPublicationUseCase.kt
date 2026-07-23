package nl.rhaydus.nestbox.core.content.domain.usecase

import nl.rhaydus.common.runCatchingLogged
import nl.rhaydus.nestbox.core.content.domain.model.Publication
import nl.rhaydus.nestbox.core.content.domain.repository.PublicationRepository

class GetPublicationUseCase(private val publicationRepository: PublicationRepository) {
    suspend operator fun invoke(id: String): Result<Publication> =
        runCatchingLogged("Could not load publication '$id'") {
            publicationRepository.getPublication(id)
        }
}
