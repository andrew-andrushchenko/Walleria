package com.andrii_a.walleria.ui.user_details

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
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.andrii_a.walleria.ui.collection_details.navigateToCollectionDetails
import com.andrii_a.walleria.ui.common.UserNickname
import com.andrii_a.walleria.ui.navigation.Screen
import com.andrii_a.walleria.ui.photo_details.navigateToPhotoDetails
import com.andrii_a.walleria.ui.profile_edit.navigateToEditUserProfile
import com.andrii_a.walleria.ui.search.navigateToSearch

fun NavGraphBuilder.userDetailsRoute(navController: NavController) {
    composable(
        route = "${Screen.UserDetails.route}/{${UserDetailsArgs.NICKNAME}}",
        arguments = listOf(
            navArgument(UserDetailsArgs.NICKNAME) {
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
        }
    ) {
        val viewModel: UserDetailsViewModel = hiltViewModel()

        val loadResult by viewModel.loadResult.collectAsStateWithLifecycle()
        val photosLayoutType by viewModel.photosLayoutType.collectAsStateWithLifecycle()
        val collectionsLayoutType by viewModel.collectionsLayoutType.collectAsStateWithLifecycle()
        val photosLoadQuality by viewModel.photosLoadQuality.collectAsStateWithLifecycle()

        UserDetailsScreen(
            loadResult = loadResult,
            photosListLayoutType = photosLayoutType,
            collectionsListLayoutType = collectionsLayoutType,
            photosLoadQuality = photosLoadQuality,
            onRetryLoading = viewModel::getUser,
            navigateBack = navController::navigateUp,
            navigateToPhotoDetails = navController::navigateToPhotoDetails,
            navigateToCollectionDetails = navController::navigateToCollectionDetails,
            navigateToEditUserProfile = navController::navigateToEditUserProfile,
            navigateToSearch = navController::navigateToSearch,
            navigateToUserDetails = navController::navigateToUserDetails
        )
    }
}

fun NavController.navigateToUserDetails(userNickname: UserNickname) {
    this.navigate("${Screen.UserDetails.route}/${userNickname.value}")
}

object UserDetailsArgs {
    const val NICKNAME = "userNickname"
}