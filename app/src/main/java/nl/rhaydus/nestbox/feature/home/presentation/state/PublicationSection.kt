package nl.rhaydus.nestbox.feature.home.presentation.state

import nl.rhaydus.nestbox.core.content.domain.model.PublicationSummary
import nl.rhaydus.nestbox.core.content.domain.model.PublicationType

internal data class PublicationSection(
    val type: PublicationType,
    val publications: List<PublicationSummary>,
)
