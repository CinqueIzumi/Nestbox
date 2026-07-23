package nl.rhaydus.nestbox.core.content.data.model

import kotlinx.serialization.Serializable

@Serializable
data class TreeEntryResponse(
    val path: String,
    val sha: String,
    val type: String,
)
