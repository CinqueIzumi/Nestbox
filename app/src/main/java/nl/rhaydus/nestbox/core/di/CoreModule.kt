package nl.rhaydus.nestbox.core.di

import kotlinx.coroutines.Dispatchers
import nl.rhaydus.nestbox.core.presentation.dispatchers.AppDispatchers
import org.koin.dsl.module

val coreModule = module {
    single {
        AppDispatchers(
            main = Dispatchers.Main,
            io = Dispatchers.IO,
            default = Dispatchers.Default,
        )
    }
}