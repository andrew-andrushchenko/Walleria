package com.andrii_a.walleria.ui.photo_details

import android.app.Activity
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.andrii_a.walleria.ui.collect_photo.navigateToCollectPhoto
import com.andrii_a.walleria.ui.collection_details.navigateToCollectionDetails
import com.andrii_a.walleria.ui.common.PhotoId
import com.andrii_a.walleria.ui.navigation.Screen
import com.andrii_a.walleria.ui.search.navigateToSearch
import com.andrii_a.walleria.ui.user_details.navigateToUserDetails
import com.andrii_a.walleria.ui.util.InterScreenCommunicationKeys

fun NavGraphBuilder.photoDetailsRoute(navController: NavController) {
    composable(
        route = "${Screen.PhotoDetails.route}/{${PhotoDetailsArgs.ID}}",
        arguments = listOf(
            navArgument(PhotoDetailsArgs.ID) {
                type = NavType.StringType
                nullable = false
            }
        ),
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
        },
    ) {
        val viewModel: PhotoDetailsViewModel = hiltViewModel()

        val loadResult by viewModel.loadResult.collectAsStateWithLifecycle()

        val shouldUseDarkIcons = !isSystemInDarkTheme()
        val view = LocalView.current

        DisposableEffect(key1 = loadResult) {
            when (loadResult) {
                is PhotoLoadResult.Empty,
                is PhotoLoadResult.Error,
                is PhotoLoadResult.Loading -> Unit

                is PhotoLoadResult.Success -> {
                    val window = (view.context as Activity).window
                    WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
                }
            }

            onDispose {
                when (loadResult) {
                    is PhotoLoadResult.Empty,
                    is PhotoLoadResult.Error,
                    is PhotoLoadResult.Loading -> Unit

                    is PhotoLoadResult.Success -> {
                        val window = (view.context as Activity).window
                        WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = shouldUseDarkIcons
                    }
                }
            }
        }

        val isUserLoggedIn by viewModel.isUserLoggedIn.collectAsStateWithLifecycle()
        val photosDownloadQuality by viewModel.photosDownloadQuality.collectAsStateWithLifecycle()
        val isPhotoLiked by viewModel.isLiked.collectAsStateWithLifecycle()
        val isPhotoCollected by viewModel.isCollected.collectAsStateWithLifecycle()

        val collectResult = navController.currentBackStackEntry
            ?.savedStateHandle
            ?.getStateFlow(InterScreenCommunicationKeys.COLLECT_SCREEN_RESULT_KEY, isPhotoCollected)
            ?.collectAsStateWithLifecycle()

        LaunchedEffect(key1 = collectResult?.value) {
            viewModel.onEvent(
                if (collectResult?.value == true) {
                    PhotoDetailsEvent.CollectPhoto
                } else {
                    PhotoDetailsEvent.DropPhoto
                }
            )
        }

        PhotoDetailsScreen(
            loadResult = loadResult,
            isUserLoggedIn = isUserLoggedIn,
            isPhotoLiked = isPhotoLiked,
            isPhotoCollected = isPhotoCollected,
            photosDownloadQuality = photosDownloadQuality,
            onEvent = viewModel::onEvent,
            navigateBack = navController::navigateUp,
            navigateToUserDetails = navController::navigateToUserDetails,
            navigateToCollectPhoto = navController::navigateToCollectPhoto,
            navigateToSearch = navController::navigateToSearch,
            navigateToCollectionDetails = navController::navigateToCollectionDetails
        )
    }
}

fun NavController.navigateToPhotoDetails(photoId: PhotoId) =
    this.navigate("${Screen.PhotoDetails.route}/${photoId.value}")

object PhotoDetailsArgs {
    const val ID = "photoId"
}