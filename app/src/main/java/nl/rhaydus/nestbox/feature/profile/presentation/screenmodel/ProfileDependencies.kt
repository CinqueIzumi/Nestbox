package nl.rhaydus.nestbox.feature.profile.presentation.screenmodel

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import nl.rhaydus.nestbox.core.presentation.toad.ActionDependencies

class ProfileDependencies(
    override val coroutineScope: CoroutineScope,
    override val mainDispatcher: CoroutineDispatcher,
) : ActionDependencies()
