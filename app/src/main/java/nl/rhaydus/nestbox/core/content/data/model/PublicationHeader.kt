package nl.rhaydus.nestbox.core.content.data.model

/** The `## #7 2026-06-12 Weekly` line every publication opens with. */
data class PublicationHeader(
    val number: Int,
    val date: String,
    val marker: String,
)
