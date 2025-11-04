package com.andrii_a.walleria.ui.main

import android.content.ComponentName
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.browser.customtabs.CustomTabsCallback
import androidx.browser.customtabs.CustomTabsClient
import androidx.browser.customtabs.CustomTabsService
import androidx.browser.customtabs.CustomTabsServiceConnection
import androidx.browser.customtabs.CustomTabsSession
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
import androidx.core.net.toUri
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.andrii_a.walleria.data.util.Config
import com.andrii_a.walleria.domain.AppTheme
import com.andrii_a.walleria.domain.repository.LocalPreferencesRepository
import com.andrii_a.walleria.ui.util.CustomTabsHelper
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {

    private var customTabsClient: CustomTabsClient? = null
    private var customTabsSession: CustomTabsSession? = null
    private var shouldUnbindCustomTabService = false

    private val customTabsServiceConnection = object : CustomTabsServiceConnection() {
        override fun onCustomTabsServiceConnected(
            name: ComponentName,
            client: CustomTabsClient
        ) {
            customTabsClient = client
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            customTabsClient = null
        }
    }

    private val localPreferencesRepository by inject<LocalPreferencesRepository>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        enableEdgeToEdge()

        setupCustomTabs()

        setContent {
            val appTheme by localPreferencesRepository.appTheme.collectAsStateWithLifecycle(
                initialValue = runBlocking { localPreferencesRepository.appTheme.first() }
            )

            val useDarkTheme = when (appTheme) {
                AppTheme.SYSTEM_DEFAULT -> isSystemInDarkTheme()
                AppTheme.LIGHT -> false
                AppTheme.DARK -> true
            }

            WalleriaApp(darkTheme = useDarkTheme)
        }
    }

    private fun setupCustomTabs() {
        CustomTabsHelper.getPackageNameToUse(this)?.let { customTabsPackageName ->
            if (CustomTabsClient.bindCustomTabsService(
                    this,
                    customTabsPackageName,
                    customTabsServiceConnection
                )
            ) {
                shouldUnbindCustomTabService = true
                customTabsClient?.warmup(0)
                customTabsSession = customTabsClient?.newSession(CustomTabsCallback())?.apply {
                    mayLaunchUrl(
                        Config.LOGIN_URL.toUri(),
                        null,
                        mutableListOf(
                            Bundle().apply {
                                putParcelable(
                                    CustomTabsService.KEY_URL,
                                    Config.JOIN_URL.toUri()
                                )
                            }
                        )
                    )
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (shouldUnbindCustomTabService) {
            unbindService(customTabsServiceConnection)
            shouldUnbindCustomTabService = false
        }
        customTabsClient = null
        customTabsSession = null
    }
}