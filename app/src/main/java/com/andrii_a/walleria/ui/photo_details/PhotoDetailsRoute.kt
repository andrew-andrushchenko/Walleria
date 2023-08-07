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
        val isPhotoCollected = viewModel.isCollected.collectAsStateWithLifecycle()

        val collectResult = navController.currentBackStackEntry
            ?.savedStateHandle
            ?.getStateFlow("collect_result_key", isPhotoCollected.value)
            ?.collectAsStateWithLifecycle()

        collectResult?.value?.let { isCollected ->
            viewModel.onEvent(
                if (isCollected) PhotoDetailsEvent.CollectPhoto
                else PhotoDetailsEvent.DropPhoto
            )
        }

        PhotoDetailsScreen(
            photoId = photoId,
            loadResult = loadResultState.value,
            isUserLoggedIn = isUserLoggedIn.value,
            isPhotoLiked = isPhotoLiked.value,
            isPhotoCollected = isPhotoCollected.value,
            onEvent = viewModel::onEvent,
            navigateBack = navController::navigateUp,
            navigateToUserDetails = {},
            navigateToCollectPhoto = navController::navigateToCollectPhoto,
            navigateToSearch = navController::navigateToSearch
        )
    }
}

fun NavController.navigateToPhotoDetails(photoId: PhotoId) =
    this.navigate("${Screen.PhotoDetails.route}/${photoId.value}")

object PhotoDetailsArgs {
    const val ID = "photoId"
}