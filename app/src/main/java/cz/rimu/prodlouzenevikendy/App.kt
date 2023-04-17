package cz.rimu.prodlouzenevikendy

import android.app.Application
import cz.rimu.prodlouzenevikendy.di.appModule
import cz.rimu.prodlouzenevikendy.di.networkModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App: Application() {
    override fun onCreate() {
        super.onCreate()

        initKoin()
    }

    private fun initKoin() {
        startKoin {
            // Android context
            androidContext(this@App)
            // modules
            modules(listOf(networkModule, appModule))
        }
    }
}