package nl.rhaydus.nestbox.feature.profile.di

import nl.rhaydus.nestbox.feature.profile.presentation.collector.ProfileCollector
import nl.rhaydus.nestbox.feature.profile.presentation.screenmodel.ProfileScreenModel
import org.koin.dsl.module

val profileModule = module {
    single<List<ProfileCollector>> { emptyList() }

    factory {
        ProfileScreenModel(
            appDispatchers = get(),
            flows = get(),
        )
    }
}
