package com.andrii_a.walleria.ui.account

import android.content.Intent
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.andrii_a.walleria.ui.login.LoginActivity
import com.andrii_a.walleria.ui.navigation.Screen
import com.andrii_a.walleria.ui.util.collectAsOneTimeEvents
import org.koin.androidx.compose.koinViewModel

fun NavGraphBuilder.accountNavigationRoute(navController: NavController) {
    composable<Screen.AccountAndSettings> {

        val viewModel: AccountViewModel = koinViewModel()
        val state by viewModel.state.collectAsStateWithLifecycle()

        val context = LocalContext.current

        viewModel.navigationEventsChannelFlow.collectAsOneTimeEvents { event ->
            when (event) {
                is AccountScreenNavigationEvent.NavigateToAboutScreen -> {
                    navController.navigate(Screen.About)
                }

                is AccountScreenNavigationEvent.NavigateToEditAccountScreen -> {
                    navController.navigate(Screen.EditUserProfile)
                }

                is AccountScreenNavigationEvent.NavigateToLoginScreen -> {
                    Intent(context, LoginActivity::class.java).also {
                        context.startActivity(it)
                    }
                }

                is AccountScreenNavigationEvent.NavigateToSettingsScreen -> {
                    navController.navigate(Screen.Settings)
                }

                is AccountScreenNavigationEvent.NavigateToViewAccountScreen -> {
                    navController.navigate(Screen.UserDetails(event.nickname))
                }
            }
        }

        ProfileScreen(
            state = state,
            onEvent = viewModel::onEvent
        )
    }
}