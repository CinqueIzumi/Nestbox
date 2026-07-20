package nl.rhaydus.nestbox

import android.app.Application
import nl.rhaydus.common.AppLog
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class NestboxApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Foundation code logs through AppLog; without this no writer is installed and every call is
        // a no-op. Debug builds only, so release stays silent.
        AppLog.install(
            tag = "Nestbox",
            debug = BuildConfig.DEBUG,
        )

        startKoin {
            androidContext(this@NestboxApplication)

            modules(nestboxModules)
        }
    }
}
