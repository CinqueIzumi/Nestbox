package nl.rhaydus.nestbox.core.auth.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AccessTokenResponse(
    @SerialName("access_token")
    val accessToken: String? = null,

    @SerialName("token_type")
    val tokenType: String? = null,

    val scope: String? = null,

    val error: String? = null,

    @SerialName("error_description")
    val errorDescription: String? = null,
)
