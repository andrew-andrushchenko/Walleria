package com.andrii_a.walleria.ui.login

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.core.util.Consumer
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.andrii_a.walleria.R
import com.andrii_a.walleria.data.util.Config
import com.andrii_a.walleria.ui.main.MainActivity
import com.andrii_a.walleria.ui.navigation.Screen
import com.andrii_a.walleria.ui.util.CustomTabsHelper
import com.andrii_a.walleria.ui.util.collectAsOneTimeEvents
import com.andrii_a.walleria.ui.util.toast
import org.koin.androidx.compose.koinViewModel

fun NavGraphBuilder.loginRoute(navController: NavController) {
    composable<Screen.Login> {
        val viewModel: LoginViewModel = koinViewModel()

        val activity = LocalActivity.current as MainActivity

        DisposableEffect(key1 = Unit) {
            val listener = Consumer<Intent> { intent ->
                intent.data?.let { uri ->
                    if (uri.authority.equals(Config.AUTH_CALLBACK.substringAfterLast("/"))) {
                        uri.getQueryParameter("code")?.let { code ->
                            viewModel.onEvent(LoginEvent.GetAccessToken(code))
                        }
                    }
                }
            }

            activity.addOnNewIntentListener(listener)

            onDispose {
                activity.removeOnNewIntentListener(listener)
            }
        }

        val state by viewModel.state.collectAsStateWithLifecycle()

        LaunchedEffect(key1 = state) {
            when {
                state.error != null -> {
                    activity.applicationContext.toast(R.string.login_failed)
                    viewModel.onEvent(LoginEvent.DismissError)
                }

                state.isTokenObtained && !state.isUserDataSaved -> {
                    viewModel.onEvent(LoginEvent.PerformSaveUserProfile)
                }

                state.isLoggedIn -> {
                    navController.navigateUp()
                }
            }
        }

        viewModel.navigationEventFlow.collectAsOneTimeEvents { event ->
            when (event) {
                LoginNavigationEvent.NavigateToLoginCustomTab -> {
                    CustomTabsHelper.openCustomTab(activity, Uri.parse(Config.LOGIN_URL))
                }

                LoginNavigationEvent.NavigateToJoinCustomTab -> {
                    CustomTabsHelper.openCustomTab(activity, Uri.parse(Config.JOIN_URL))
                }

                LoginNavigationEvent.NavigateBack -> {
                    navController.navigateUp()
                }
            }
        }

        LoginScreen(
            state = state,
            onEvent = viewModel::onEvent
        )
    }
}