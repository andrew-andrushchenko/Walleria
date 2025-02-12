package com.andrii_a.walleria.ui.account

import android.content.Intent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.andrii_a.walleria.ui.login.LoginActivity
import com.andrii_a.walleria.ui.navigation.Screen
import com.andrii_a.walleria.ui.util.collectAsOneTimeEvents

fun NavGraphBuilder.accountNavigationRoute(navController: NavController) {
    composable<Screen.AccountAndSettings>(
        enterTransition = {
            fadeIn(
                animationSpec = tween(300, easing = LinearEasing)
            ) + slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Up,
                animationSpec = spring(stiffness = Spring.StiffnessMedium)
            )
        },
        popEnterTransition = {
            fadeIn(
                animationSpec = tween(300, easing = LinearEasing)
            ) + slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Up,
                animationSpec = spring(stiffness = Spring.StiffnessMedium)
            )
        }
    ) {

        val viewModel: AccountViewModel = hiltViewModel()
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