package com.example.cailights

import android.app.Application
import com.example.cailights.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class CailightsApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@CailightsApp)
            modules(appModule)
        }
    }
}
