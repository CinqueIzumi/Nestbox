package nl.rhaydus.nestbox.core.auth.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DeviceCodeResponse(
    @SerialName("device_code")
    val deviceCode: String,

    @SerialName("user_code")
    val userCode: String,

    @SerialName("verification_uri")
    val verificationUri: String,

    @SerialName("expires_in")
    val expiresIn: Int,

    val interval: Int,
)
