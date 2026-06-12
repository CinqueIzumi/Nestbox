package nl.rhaydus.nestbox.feature.profile.presentation.state

import nl.rhaydus.nestbox.core.presentation.toad.LocalVariables

data class ProfileLocalVariables(
    val deviceCode: String? = null,
) : LocalVariables
