package nl.rhaydus.nestbox.core.content.data.model

import kotlinx.serialization.Serializable

@Serializable
data class CommitResponse(val sha: String)
