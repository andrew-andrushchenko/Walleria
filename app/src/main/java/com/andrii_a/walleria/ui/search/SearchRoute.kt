package com.andrii_a.walleria.ui.search

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.andrii_a.walleria.ui.navigation.Screen
import com.andrii_a.walleria.ui.util.collectAsOneTimeEvents

fun NavGraphBuilder.searchRoute(navController: NavController) {
    composable<Screen.Search>(
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
        val viewModel: SearchViewModel = hiltViewModel()

        val state by viewModel.state.collectAsStateWithLifecycle()

        viewModel.navigationEventsChannelFlow.collectAsOneTimeEvents { event ->
            when (event) {
                is SearchNavigationEvent.NavigateBack -> {
                    navController.navigateUp()
                }

                is SearchNavigationEvent.NavigateToPhotoDetails -> {
                    navController.navigate(Screen.PhotoDetails(event.photoId))
                }

                is SearchNavigationEvent.NavigateToCollectionDetails -> {
                    navController.navigate(Screen.CollectionDetails(event.collectionId))
                }

                is SearchNavigationEvent.NavigateToUserDetails -> {
                    navController.navigate(Screen.UserDetails(event.userNickname))
                }
            }
        }

        SearchScreen(
            state = state,
            onEvent = viewModel::onEvent
        )
    }
}
