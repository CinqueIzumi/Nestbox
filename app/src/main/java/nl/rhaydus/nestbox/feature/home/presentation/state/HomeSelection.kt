package nl.rhaydus.nestbox.feature.home.presentation.state

import nl.rhaydus.nestbox.core.content.domain.model.PublicationSummary
import nl.rhaydus.nestbox.core.content.domain.model.PublicationType

/**
 * The hero + sections split shown for a given filter, derived from the full newest-first list.
 * Shared by [LoadPublicationsAction][nl.rhaydus.nestbox.feature.home.presentation.action.LoadPublicationsAction]
 * and [SelectPublicationFilterAction][nl.rhaydus.nestbox.feature.home.presentation.action.SelectPublicationFilterAction]
 * so the split logic exists exactly once.
 */
internal data class HomeSelection(
    val hero: PublicationSummary?,
    val sections: List<PublicationSection>,
)

internal fun deriveHomeSelection(
    publications: List<PublicationSummary>,
    filter: PublicationFilter,
): HomeSelection {
    val matching = publications.filter { publication -> filter.matches(publication.type) }
    val hero = matching.firstOrNull()

    val sections = PublicationType.entries
        .filter { type -> filter.matches(type) }
        .mapNotNull { type ->
            val publicationsForType = matching.filter { publication ->
                publication.type == type && publication.id != hero?.id
            }

            if (publicationsForType.isEmpty()) return@mapNotNull null

            PublicationSection(
                type = type,
                publications = publicationsForType,
            )
        }

    return HomeSelection(
        hero = hero,
        sections = sections,
    )
}

private fun PublicationFilter.matches(type: PublicationType): Boolean = when (this) {
    PublicationFilter.ALL -> true
    PublicationFilter.WEEKLY_LETTERS -> type == PublicationType.WEEKLY_LETTER
    PublicationFilter.ARTICLES -> type == PublicationType.ARTICLE
    PublicationFilter.INTERVIEW_PREP -> type == PublicationType.INTERVIEW_PREP
}
