package com.andrii_a.walleria.ui.login

import android.content.ComponentName
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.browser.customtabs.CustomTabsCallback
import androidx.browser.customtabs.CustomTabsClient
import androidx.browser.customtabs.CustomTabsService
import androidx.browser.customtabs.CustomTabsServiceConnection
import androidx.browser.customtabs.CustomTabsSession
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.core.view.WindowCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.andrii_a.walleria.R
import com.andrii_a.walleria.data.util.UNSPLASH_AUTH_CALLBACK
import com.andrii_a.walleria.ui.theme.WalleriaTheme
import com.andrii_a.walleria.ui.util.CustomTabsHelper
import com.andrii_a.walleria.ui.util.toast
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginActivity : ComponentActivity() {

    private val viewModel: LoginViewModel by viewModels()

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
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setupCustomTabs()

        setContent {
            val systemUiController = rememberSystemUiController()

            LaunchedEffect(key1 = true) {
                systemUiController.setSystemBarsColor(color = Color.Transparent)
            }

            val state by viewModel.loginState.collectAsStateWithLifecycle()

            LaunchedEffect(key1 = state) {
                when (state) {
                    is LoginState.Empty -> Unit
                    is LoginState.Loading -> Unit
                    is LoginState.Error -> {
                        applicationContext.toast(R.string.login_failed)
                    }

                    is LoginState.Success -> {
                        viewModel.retrieveAndSaveUserData((state as LoginState.Success).accessToken)
                        applicationContext.toast(R.string.login_successful)
                        finish()
                    }
                }
            }

            WalleriaTheme {
                LoginScreen(
                    isLoading = state is LoginState.Loading,
                    onLoginClicked = { openChromeCustomTab(viewModel.loginUrl) },
                    onJoinClicked = { openChromeCustomTab(viewModel.joinUrl) },
                    onNavigateBack = ::finish
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.data?.let { uri ->
            if (uri.authority.equals(UNSPLASH_AUTH_CALLBACK)) {
                uri.getQueryParameter("code")?.let { code ->
                    lifecycleScope.launch {
                        repeatOnLifecycle(Lifecycle.State.RESUMED) {
                            viewModel.getAccessToken(code = code)
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
                        Uri.parse(viewModel.loginUrl),
                        null,
                        mutableListOf(
                            Bundle().apply {
                                putParcelable(
                                    CustomTabsService.KEY_URL,
                                    Uri.parse(viewModel.joinUrl)
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