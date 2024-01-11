package com.andrii_a.walleria.ui.photos

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
import com.andrii_a.walleria.ui.navigation.NavigationScreen
import com.andrii_a.walleria.ui.photo_details.navigateToPhotoDetails
import com.andrii_a.walleria.ui.search.navigateToSearch
import com.andrii_a.walleria.ui.user_details.navigateToUserDetails
import com.andrii_a.walleria.ui.util.collectAsOneTimeEvents

fun NavGraphBuilder.photosBottomNavRoute(
    navController: NavController,
    openProfileBottomSheet: () -> Unit
) {
    composable(
        route = NavigationScreen.Photos.route,
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
        val viewModel: PhotosViewModel = hiltViewModel()

        val state by viewModel.state.collectAsStateWithLifecycle()

        viewModel.navigationEventsChannelFlow.collectAsOneTimeEvents { event ->
            when (event) {
                is PhotosNavigationEvent.NavigateToPhotoDetailsScreen -> {
                    navController.navigateToPhotoDetails(event.photoId)
                }

                is PhotosNavigationEvent.NavigateToUserDetails -> {
                    navController.navigateToUserDetails(event.userNickname)
                }

                is PhotosNavigationEvent.NavigateToSearchScreen -> {
                    navController.navigateToSearch()
                }

                is PhotosNavigationEvent.NavigateToProfileScreen -> {
                    openProfileBottomSheet()
                }
            }
        }

        PhotosScreen(
            state = state,
            onEvent = viewModel::onEvent
        )
    }
}