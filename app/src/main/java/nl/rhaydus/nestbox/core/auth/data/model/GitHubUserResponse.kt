package nl.rhaydus.nestbox.core.auth.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GitHubUserResponse(
    val login: String,

    @SerialName("avatar_url")
    val avatarUrl: String,
)
