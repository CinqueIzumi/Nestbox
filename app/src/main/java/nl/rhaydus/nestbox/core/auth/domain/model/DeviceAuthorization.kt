package nl.rhaydus.nestbox.core.auth.domain.model

data class DeviceAuthorization(
    val userCode: String,
    val verificationUri: String,
    val deviceCode: String,
)
