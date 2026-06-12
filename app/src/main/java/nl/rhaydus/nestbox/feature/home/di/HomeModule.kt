package nl.rhaydus.nestbox.feature.home.di

import nl.rhaydus.nestbox.feature.home.presentation.collector.HomeCollector
import nl.rhaydus.nestbox.feature.home.presentation.screenmodel.HomeScreenModel
import org.koin.dsl.module

val homeModule = module {
    single<List<HomeCollector>> { emptyList() }

    factory {
        HomeScreenModel(
            appDispatchers = get(),
            flows = get(),
        )
    }
}
 