package nl.rhaydus.nestbox.feature.home.presentation.screenmodel

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import nl.rhaydus.nestbox.core.content.domain.usecase.GetPublicationsUseCase
import nl.rhaydus.toad.ActionDependencies

class HomeDependencies(
    override val coroutineScope: CoroutineScope,
    override val mainDispatcher: CoroutineDispatcher,
    val getPublicationsUseCase: GetPublicationsUseCase,
) : ActionDependencies()
