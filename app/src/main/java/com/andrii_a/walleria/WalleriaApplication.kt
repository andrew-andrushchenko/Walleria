package com.andrii_a.walleria

import android.app.Application
import com.andrii_a.walleria.domain.ApplicationScope
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import javax.inject.Inject

@HiltAndroidApp
class WalleriaApplication : Application() {

    @Inject
    @ApplicationScope
    lateinit var applicationScope: CoroutineScope

    override fun onTerminate() {
        applicationScope.cancel()
        super.onTerminate()
    }
}