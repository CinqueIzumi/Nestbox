package nl.rhaydus.nestbox.core.content.domain.model

data class PublicationSummary(
    val id: String,
    val type: PublicationType,
    val number: Int?,
    val date: String,
    val previewText: String,
)
