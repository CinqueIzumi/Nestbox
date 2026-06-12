package nl.rhaydus.nestbox.feature.home.di

import nl.rhaydus.nestbox.feature.home.presentation.screenmodel.HomeScreenModel
import org.koin.dsl.module

val homeModule = module {
    // Collectors are passed straight into the factory rather than bound as a List<HomeCollector>:
    // generics are erased at runtime, so every List<…Collector> would collide under one Koin key.
    factory {
        HomeScreenModel(
            appDispatchers = get(),
            flows = emptyList(),
        )
    }
}
