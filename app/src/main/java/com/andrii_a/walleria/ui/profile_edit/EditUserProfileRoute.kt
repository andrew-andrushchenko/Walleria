package com.andrii_a.walleria.ui.profile_edit

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.andrii_a.walleria.ui.navigation.Screen
import com.andrii_a.walleria.ui.util.collectAsOneTimeEvents
import com.andrii_a.walleria.ui.util.toast

fun NavGraphBuilder.editUserProfileRoute(navController: NavController) {
    composable<Screen.EditUserProfile>(
        enterTransition = {
            fadeIn(
                animationSpec = tween(300, easing = LinearEasing)
            ) + slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Start,
                animationSpec = spring(stiffness = Spring.StiffnessMedium)
            )
        },
        exitTransition = {
            fadeOut(
                animationSpec = tween(300, easing = LinearEasing)
            ) + slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.End,
                animationSpec = spring(stiffness = Spring.StiffnessMedium)
            )
        }
    ) {
        val viewModel: EditUserProfileViewModel = hiltViewModel()

        val state by viewModel.state.collectAsStateWithLifecycle()

        val context = LocalContext.current
        viewModel.profileUpdateMessageFlow.collectAsOneTimeEvents { uiText ->
            context.toast(uiText.asString(context))
        }

        viewModel.navigationEventsChannelFlow.collectAsOneTimeEvents { event ->
            when (event) {
                is EditUserProfileNavigationEvent.NavigateBack -> {
                    navController.navigateUp()
                }
            }
        }

        EditUserProfileScreen(
            state = state,
            onEvent = viewModel::onEvent
        )
    }
}
