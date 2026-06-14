package nl.rhaydus.nestbox.core.content.domain.usecase

import nl.rhaydus.nestbox.core.common.runCatchingCancellable
import nl.rhaydus.nestbox.core.content.domain.model.Publication
import nl.rhaydus.nestbox.core.content.domain.repository.PublicationRepository

class GetPublicationUseCase(private val publicationRepository: PublicationRepository) {
    suspend operator fun invoke(id: String): Result<Publication> =
        runCatchingCancellable { publicationRepository.getPublication(id) }
}
