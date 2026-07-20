package nl.rhaydus.nestbox

import nl.rhaydus.nestbox.core.auth.di.authModule
import nl.rhaydus.nestbox.core.content.di.contentModule
import nl.rhaydus.nestbox.core.di.coreModule
import nl.rhaydus.nestbox.feature.home.di.homeModule
import nl.rhaydus.nestbox.feature.profile.di.profileModule
import nl.rhaydus.nestbox.feature.publication.di.publicationModule

val nestboxModules = listOf(
    coreModule,
    authModule,
    contentModule,
    homeModule,
    publicationModule,
    profileModule,
)
