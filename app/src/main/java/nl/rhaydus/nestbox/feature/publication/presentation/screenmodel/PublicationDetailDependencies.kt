package nl.rhaydus.nestbox.feature.publication.presentation.screenmodel

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import nl.rhaydus.nestbox.core.content.domain.usecase.GetPublicationUseCase
import nl.rhaydus.nestbox.core.presentation.markdown.MarkdownParser
import nl.rhaydus.nestbox.core.presentation.toad.ActionDependencies

class PublicationDetailDependencies(
    override val coroutineScope: CoroutineScope,
    override val mainDispatcher: CoroutineDispatcher,
    val getPublicationUseCase: GetPublicationUseCase,
    val markdownParser: MarkdownParser,
    val defaultDispatcher: CoroutineDispatcher,
) : ActionDependencies()
