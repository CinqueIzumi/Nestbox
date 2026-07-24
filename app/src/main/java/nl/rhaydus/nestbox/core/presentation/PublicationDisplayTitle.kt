package nl.rhaydus.nestbox.core.presentation

private const val DEFAULT_PUBLICATION_TITLE = "The Doveletter"

/**
 * The display title for a publication (design system §0): a weekly letter carries no title of its
 * own, so it wears the newsletter's own name instead. The one place the brand fallback is chosen.
 */
internal fun publicationDisplayTitle(title: String?): String = title ?: DEFAULT_PUBLICATION_TITLE
