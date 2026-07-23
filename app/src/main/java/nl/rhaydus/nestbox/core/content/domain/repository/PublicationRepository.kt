package nl.rhaydus.nestbox.core.content.domain.repository

import nl.rhaydus.nestbox.core.content.domain.model.Publication
import nl.rhaydus.nestbox.core.content.domain.model.PublicationSummary

interface PublicationRepository {
    suspend fun getPublications(): List<PublicationSummary>

    suspend fun getPublication(id: String): Publication

    /** Drops every synced publication, for when the subscription can no longer be read. */
    suspend fun clearLocalContent()
}
