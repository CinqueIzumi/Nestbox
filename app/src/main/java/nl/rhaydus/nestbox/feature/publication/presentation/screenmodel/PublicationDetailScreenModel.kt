package nl.rhaydus.nestbox.feature.publication.presentation.screenmodel

import cafe.adriel.voyager.core.model.screenModelScope
import nl.rhaydus.common.AppDispatchers
import nl.rhaydus.nestbox.core.content.domain.usecase.GetPublicationUseCase
import nl.rhaydus.nestbox.core.presentation.markdown.MarkdownParser
import nl.rhaydus.nestbox.feature.publication.presentation.action.LoadPublicationAction
import nl.rhaydus.nestbox.feature.publication.presentation.action.PublicationDetailAction
import nl.rhaydus.nestbox.feature.publication.presentation.collector.PublicationDetailCollector
import nl.rhaydus.nestbox.feature.publication.presentation.event.PublicationDetailEvent
import nl.rhaydus.nestbox.feature.publication.presentation.state.PublicationDetailLocalVariables
import nl.rhaydus.nestbox.feature.publication.presentation.state.PublicationDetailUiState
import nl.rhaydus.toad.ToadScreenModel

class PublicationDetailScreenModel(
    private val publicationId: String,
    private val appDispatchers: AppDispatchers,
    private val getPublicationUseCase: GetPublicationUseCase,
    private val markdownParser: MarkdownParser,
    flows: List<PublicationDetailCollector>,
) : ToadScreenModel<PublicationDetailUiState, PublicationDetailEvent, PublicationDetailDependencies, PublicationDetailCollector, PublicationDetailLocalVariables>(
    initialState = PublicationDetailUiState(),
    initialLocalVariables = PublicationDetailLocalVariables(),
    initializers = flows,
) {
    override val dependencies: PublicationDetailDependencies = PublicationDetailDependencies(
        mainDispatcher = appDispatchers.main,
        coroutineScope = screenModelScope,
        getPublicationUseCase = getPublicationUseCase,
        markdownParser = markdownParser,
        defaultDispatcher = appDispatchers.default,
    )

    init {
        startInitializers()
        dispatch(LoadPublicationAction(publicationId))
    }

    fun runAction(action: PublicationDetailAction) = dispatch(action = action)
}
