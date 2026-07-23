package nl.rhaydus.nestbox.core.content.data.model

import kotlinx.serialization.Serializable

@Serializable
data class TreeResponse(
    val tree: List<TreeEntryResponse>,
    val truncated: Boolean = false,
)
