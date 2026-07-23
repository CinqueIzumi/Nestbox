package nl.rhaydus.nestbox.core.content.di

import nl.rhaydus.common.AppDispatchers
import nl.rhaydus.nestbox.BuildConfig
import nl.rhaydus.nestbox.core.content.data.datasource.PublicationLocalDataSource
import nl.rhaydus.nestbox.core.content.data.datasource.PublicationLocalDataSourceImpl
import nl.rhaydus.nestbox.core.content.data.datasource.PublicationRemoteDataSource
import nl.rhaydus.nestbox.core.content.data.datasource.PublicationRemoteDataSourceImpl
import nl.rhaydus.nestbox.core.content.data.mapper.PublicationMarkdownMapper
import nl.rhaydus.nestbox.core.content.data.repository.PublicationRepositoryImpl
import nl.rhaydus.nestbox.core.content.domain.repository.PublicationRepository
import nl.rhaydus.nestbox.core.content.domain.usecase.GetPublicationUseCase
import nl.rhaydus.nestbox.core.content.domain.usecase.GetPublicationsUseCase
import nl.rhaydus.nestbox.core.presentation.markdown.MarkdownParser
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val contentModule = module {
    single<PublicationRemoteDataSource> {
        PublicationRemoteDataSourceImpl(
            client = get(),
            tokenLocalDataSource = get(),
            repository = BuildConfig.DOVELETTER_REPOSITORY,
            branch = BuildConfig.DOVELETTER_BRANCH,
        )
    }

    single<PublicationLocalDataSource> { PublicationLocalDataSourceImpl(context = androidContext()) }
    single { PublicationMarkdownMapper() }

    single<PublicationRepository> {
        PublicationRepositoryImpl(
            remoteDataSource = get(),
            localDataSource = get(),
            mapper = get(),
            ioDispatcher = get<AppDispatchers>().io,
        )
    }

    single { MarkdownParser() }

    factory { GetPublicationsUseCase(publicationRepository = get()) }
    factory { GetPublicationUseCase(publicationRepository = get()) }
}
