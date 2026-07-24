package nl.rhaydus.nestbox.core.content.domain.model

data class Publication(
    val id: String,
    val type: PublicationType,
    val number: Int?,
    val date: String,
    val title: String?,
    val markdown: String,
)
