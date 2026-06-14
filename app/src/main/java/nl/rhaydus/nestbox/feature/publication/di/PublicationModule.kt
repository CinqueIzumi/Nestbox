package nl.rhaydus.nestbox.feature.publication.di

import nl.rhaydus.nestbox.feature.publication.presentation.screenmodel.PublicationDetailScreenModel
import org.koin.dsl.module

val publicationModule = module {
    factory { parameters ->
        PublicationDetailScreenModel(
            publicationId = parameters.get(),
            appDispatchers = get(),
            getPublicationUseCase = get(),
            markdownParser = get(),
            flows = emptyList(),
        )
    }
}
