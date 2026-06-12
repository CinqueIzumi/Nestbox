package nl.rhaydus.nestbox

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class NestboxApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@NestboxApplication)

            modules(nestboxModules)
        }
    }
}
