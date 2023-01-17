package com.andrii_a.walleria.ui.photo_details

import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
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
        val statusBarColor = Color.Transparent
        val navigationBarColor = Color.Transparent
        val isDark = false

        SideEffect {
            systemUiController.setStatusBarColor(
                color = statusBarColor,
                darkIcons = isDark
            )

            systemUiController.setNavigationBarColor(
                color = navigationBarColor,
                darkIcons = isDark
            )
        }

        val viewModel: PhotoDetailsViewModel = hiltViewModel()

        val photoId = PhotoId(navBackStackEntry.arguments?.getString(PhotoDetailsArgs.ID).orEmpty())
        val loadResultState = viewModel.loadResult.collectAsState()
        val isUserLoggedIn = viewModel.isUserLoggedIn.collectAsState()
        val isPhotoLiked = viewModel.isLiked.collectAsState()
        val isPhotoBookmarked = viewModel.isBookmarked.collectAsState()

        PhotoDetailsScreen(
            photoId = photoId,
            loadResult = loadResultState.value,
            isUserLoggedIn = isUserLoggedIn.value,
            isPhotoLiked = isPhotoLiked.value,
            isPhotoBookmarked = isPhotoBookmarked.value,
            dispatchPhotoDetailsEvent = viewModel::dispatchEvent,
            navigateBack = navController::navigateUp,
            navigateToUserDetails = {},
            navigateToBookmarkPhoto = { _, _ -> },
            navigateToSearch = navController::navigateToSearch
        )
    }
}

fun NavController.navigateToPhotoDetails(photoId: PhotoId) =
    this.navigate("${Screen.PhotoDetails.route}/${photoId.value}")

object PhotoDetailsArgs {
    const val ID = "photoId"
}