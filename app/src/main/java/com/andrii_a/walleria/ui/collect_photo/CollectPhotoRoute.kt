package com.andrii_a.walleria.ui.collect_photo

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.andrii_a.walleria.ui.collect_photo.event.CollectPhotoNavigationEvent
import com.andrii_a.walleria.ui.common.PhotoId
import com.andrii_a.walleria.ui.navigation.Screen
import com.andrii_a.walleria.ui.util.InterScreenCommunicationKeys
import com.andrii_a.walleria.ui.util.collectAsOneTimeEvents

fun NavGraphBuilder.collectPhotoRoute(navController: NavController) {
    composable(
        route = "${Screen.CollectPhoto.route}/{${CollectPhotoArgs.PHOTO_ID}}",
        arguments = listOf(
            navArgument(CollectPhotoArgs.PHOTO_ID) {
                type = NavType.StringType
                nullable = false
            }
        ),
        enterTransition = {
            fadeIn(
                animationSpec = tween(300, easing = LinearEasing)
            ) + slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Up,
                animationSpec = spring(stiffness = Spring.StiffnessMedium)
            )
        },
        exitTransition = {
            fadeOut(
                animationSpec = tween(300, easing = LinearEasing)
            ) + slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Down,
                animationSpec = spring(stiffness = Spring.StiffnessMedium)
            )
        }
    ) {
        val viewModel: CollectPhotoViewModel = hiltViewModel()
        val state by viewModel.state.collectAsStateWithLifecycle()

        BackHandler(
            enabled = true,
            onBack = {
                navController.previousBackStackEntry
                    ?.savedStateHandle
                    ?.set(
                        InterScreenCommunicationKeys.COLLECT_SCREEN_RESULT_KEY,
                        state.isCollected
                    )

                navController.popBackStack()
            }
        )

        viewModel.navigationEventsChannelFlow.collectAsOneTimeEvents { event ->
            when (event) {
                CollectPhotoNavigationEvent.NavigateBack -> {
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set(
                            InterScreenCommunicationKeys.COLLECT_SCREEN_RESULT_KEY,
                            state.isCollected
                        )

                    navController.popBackStack()
                }
            }
        }

        CollectPhotoScreen(
            state = state,
            onEvent = viewModel::onEvent
        )
    }
}

fun NavController.navigateToCollectPhoto(photoId: PhotoId) {
    this.navigate("${Screen.CollectPhoto.route}/$photoId")
}

object CollectPhotoArgs {
    const val PHOTO_ID = "photo_id"
}
