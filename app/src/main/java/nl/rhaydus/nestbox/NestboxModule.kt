package nl.rhaydus.nestbox

import nl.rhaydus.nestbox.core.auth.di.authModule
import nl.rhaydus.nestbox.core.di.coreModule
import nl.rhaydus.nestbox.feature.home.di.homeModule
import nl.rhaydus.nestbox.feature.profile.di.profileModule

val nestboxModules = listOf(
    coreModule,
    authModule,
    homeModule,
    profileModule,
)