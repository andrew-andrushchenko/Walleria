package com.andrii_a.walleria.ui.photo_details

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.andrii_a.walleria.ui.collect_photo.navigateToCollectPhoto
import com.andrii_a.walleria.ui.common.PhotoId
import com.andrii_a.walleria.ui.navigation.Screen
import com.andrii_a.walleria.ui.search.navigateToSearch
import com.google.accompanist.systemuicontroller.SystemUiController

fun NavGraphBuilder.photoDetailsRoute(
    navController: NavController,
    systemUiController: SystemUiController
) {
    composable(
        route = "${Screen.PhotoDetails.route}/{${PhotoDetailsArgs.ID}}",
        arguments = listOf(
            navArgument(PhotoDetailsArgs.ID) {
                type = NavType.StringType
                nullable = false
            }
        )
    ) { navBackStackEntry ->
        val systemBarsColors = Color.Transparent
        val isDark = false

        LaunchedEffect(key1 = true) {
            systemUiController.setSystemBarsColor(
                color = systemBarsColors,
                darkIcons = isDark
            )
        }

        val viewModel: PhotoDetailsViewModel = hiltViewModel()

        val photoId = PhotoId(navBackStackEntry.arguments?.getString(PhotoDetailsArgs.ID).orEmpty())
        val loadResultState = viewModel.loadResult.collectAsStateWithLifecycle()
        val isUserLoggedIn = viewModel.isUserLoggedIn.collectAsStateWithLifecycle()
        val isPhotoLiked = viewModel.isLiked.collectAsStateWithLifecycle()
        val isPhotoBookmarked = viewModel.isBookmarked.collectAsStateWithLifecycle()

        val collectResult = navController.currentBackStackEntry
            ?.savedStateHandle
            ?.getStateFlow("collect_result_key", isPhotoBookmarked.value)
            ?.collectAsStateWithLifecycle()

        collectResult?.value?.let { isCollected ->
            viewModel.dispatchEvent(
                if (isCollected) PhotoDetailsEvent.PhotoBookmarked
                else PhotoDetailsEvent.PhotoDropped
            )
        }

        PhotoDetailsScreen(
            photoId = photoId,
            loadResult = loadResultState.value,
            isUserLoggedIn = isUserLoggedIn.value,
            isPhotoLiked = isPhotoLiked.value,
            isPhotoBookmarked = isPhotoBookmarked.value,
            dispatchPhotoDetailsEvent = viewModel::dispatchEvent,
            navigateBack = navController::navigateUp,
            navigateToUserDetails = {},
            navigateToBookmarkPhoto = navController::navigateToCollectPhoto,
            navigateToSearch = navController::navigateToSearch
        )
    }
}

fun NavController.navigateToPhotoDetails(photoId: PhotoId) =
    this.navigate("${Screen.PhotoDetails.route}/${photoId.value}")

object PhotoDetailsArgs {
    const val ID = "photoId"
}