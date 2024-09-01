package com.andrii_a.walleria.ui.topic_details

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
import com.andrii_a.walleria.ui.navigation.Screen
import com.andrii_a.walleria.ui.util.collectAsOneTimeEvents
import com.andrii_a.walleria.ui.util.openLinkInBrowser

fun NavGraphBuilder.topicDetailsRoute(navController: NavController) {
    composable<Screen.TopicDetails>(
        enterTransition = {
            fadeIn(
                animationSpec = tween(300, easing = LinearEasing)
            ) + slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Start,
                animationSpec = spring(stiffness = Spring.StiffnessMedium)
            )
        },
        popEnterTransition = {
            fadeIn(
                animationSpec = tween(300, easing = LinearEasing)
            ) + slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.End,
                animationSpec = spring(stiffness = Spring.StiffnessMedium)
            )
        }
    ) {
        val viewModel: TopicDetailsViewModel = hiltViewModel()

        val state by viewModel.state.collectAsStateWithLifecycle()

        val context = LocalContext.current
        viewModel.navigationEventsChannelFlow.collectAsOneTimeEvents { event ->
            when (event) {
                is TopicDetailsNavigationEvent.NavigateBack -> {
                    navController.navigateUp()
                }

                is TopicDetailsNavigationEvent.NavigateToChromeCustomTab -> {
                    context.openLinkInBrowser(event.url)
                }

                is TopicDetailsNavigationEvent.NavigateToPhotoDetails -> {
                    navController.navigate(Screen.PhotoDetails(event.photoId))
                }

                is TopicDetailsNavigationEvent.NavigateToUserDetails -> {
                    navController.navigate(Screen.UserDetails(event.userNickname))
                }
            }
        }

        TopicDetailsScreen(
            state = state,
            onEvent = viewModel::onEvent,
        )
    }
}
