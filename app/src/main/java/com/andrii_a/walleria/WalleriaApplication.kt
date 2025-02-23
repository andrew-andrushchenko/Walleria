package com.andrii_a.walleria

import android.app.Application
import com.andrii_a.walleria.di.appPreferencesModule
import com.andrii_a.walleria.di.baseNetworkModule
import com.andrii_a.walleria.di.collectionsModule
import com.andrii_a.walleria.di.databaseModule
import com.andrii_a.walleria.di.loginModule
import com.andrii_a.walleria.di.photosModule
import com.andrii_a.walleria.di.searchModule
import com.andrii_a.walleria.di.accountAndSettingsModule
import com.andrii_a.walleria.di.topicsModule
import com.andrii_a.walleria.di.userModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class WalleriaApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@WalleriaApplication)
            modules(
                baseNetworkModule,
                collectionsModule,
                databaseModule,
                loginModule,
                photosModule,
                appPreferencesModule,
                searchModule,
                topicsModule,
                userModule,
                accountAndSettingsModule
            )
        }
    }
}