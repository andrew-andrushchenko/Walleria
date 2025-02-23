package com.andrii_a.walleria.ui.login

import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.browser.customtabs.CustomTabsCallback
import androidx.browser.customtabs.CustomTabsClient
import androidx.browser.customtabs.CustomTabsService
import androidx.browser.customtabs.CustomTabsServiceConnection
import androidx.browser.customtabs.CustomTabsSession
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.andrii_a.walleria.R
import com.andrii_a.walleria.data.util.Config
import com.andrii_a.walleria.ui.theme.WalleriaTheme
import com.andrii_a.walleria.ui.util.CustomTabsHelper
import com.andrii_a.walleria.ui.util.collectAsOneTimeEvents
import com.andrii_a.walleria.ui.util.toast
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.compose.KoinAndroidContext

class LoginActivity : ComponentActivity() {

    private val viewModel: LoginViewModel by inject()

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

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setupCustomTabs()

        setContent {
            KoinAndroidContext {
                val view = LocalView.current

                LaunchedEffect(key1 = Unit) {
                    val window = (view.context as Activity).window
                    WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
                }

                val state by viewModel.state.collectAsStateWithLifecycle()

                LaunchedEffect(key1 = state) {
                    when {
                        state.error != null -> {
                            applicationContext.toast(R.string.login_failed)
                            viewModel.onEvent(LoginEvent.DismissError)
                        }

                        state.isTokenObtained && !state.isUserDataSaved -> {
                            viewModel.onEvent(LoginEvent.PerformSaveUserProfile)
                        }

                        state.isLoggedIn -> {
                            finish()
                        }
                    }
                }

                viewModel.navigationEventFlow.collectAsOneTimeEvents { event ->
                    when (event) {
                        LoginNavigationEvent.NavigateToLoginCustomTab -> {
                            openChromeCustomTab(Config.LOGIN_URL)
                        }

                        LoginNavigationEvent.NavigateToJoinCustomTab -> {
                            openChromeCustomTab(Config.JOIN_URL)
                        }

                        LoginNavigationEvent.NavigateBack -> {
                            finish()
                        }
                    }
                }

                WalleriaTheme {
                    LoginScreenContent(
                        state = state,
                        onEvent = viewModel::onEvent,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        intent.data?.let { uri ->
            if (uri.authority.equals(Config.AUTH_CALLBACK.substringAfterLast("/"))) {
                uri.getQueryParameter("code")?.let { code ->
                    lifecycleScope.launch {
                        repeatOnLifecycle(Lifecycle.State.RESUMED) {
                            viewModel.onEvent(LoginEvent.GetAccessToken(code))
                        }
                    }
                }
            }
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
                        Uri.parse(Config.LOGIN_URL),
                        null,
                        mutableListOf(
                            Bundle().apply {
                                putParcelable(
                                    CustomTabsService.KEY_URL,
                                    Uri.parse(Config.JOIN_URL)
                                )
                            }
                        )
                    )
                }
            }
        }
    }

    private fun openChromeCustomTab(url: String) {
        CustomTabsHelper.openCustomTab(this, Uri.parse(url))
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