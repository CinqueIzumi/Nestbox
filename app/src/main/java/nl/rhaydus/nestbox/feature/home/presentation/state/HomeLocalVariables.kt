package nl.rhaydus.nestbox.feature.home.presentation.state

import nl.rhaydus.nestbox.core.content.domain.model.PublicationSummary
import nl.rhaydus.toad.LocalVariables

internal data class HomeLocalVariables(
    val publications: List<PublicationSummary> = emptyList(),
) : LocalVariables
