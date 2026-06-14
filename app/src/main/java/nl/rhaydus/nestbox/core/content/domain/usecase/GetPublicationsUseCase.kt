package nl.rhaydus.nestbox.core.content.domain.usecase

import nl.rhaydus.nestbox.core.common.runCatchingCancellable
import nl.rhaydus.nestbox.core.content.domain.model.PublicationSummary
import nl.rhaydus.nestbox.core.content.domain.repository.PublicationRepository

class GetPublicationsUseCase(private val publicationRepository: PublicationRepository) {
    suspend operator fun invoke(): Result<List<PublicationSummary>> =
        runCatchingCancellable { publicationRepository.getPublications() }
}
