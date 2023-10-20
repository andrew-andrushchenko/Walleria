package com.andrii_a.walleria.ui.collection_details

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
import com.andrii_a.walleria.ui.common.CollectionId
import com.andrii_a.walleria.ui.common.UserNickname
import com.andrii_a.walleria.ui.navigation.Screen
import com.andrii_a.walleria.ui.photo_details.navigateToPhotoDetails
import com.andrii_a.walleria.ui.user_details.navigateToUserDetails

fun NavGraphBuilder.collectionDetailsRoute(navController: NavController) {
    composable(
        route = "${Screen.CollectionDetails.route}/{${CollectionDetailsArgs.ID}}",
        arguments = listOf(
            navArgument(CollectionDetailsArgs.ID) {
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
        val viewModel: CollectionDetailsViewModel = hiltViewModel()

        val loadResult by viewModel.loadResult.collectAsStateWithLifecycle()
        val loggedInUsername by viewModel.loggedInUsername.collectAsStateWithLifecycle()
        val photosLayoutType by viewModel.photosLayoutType.collectAsStateWithLifecycle()
        val photosLoadQuality by viewModel.photosLoadQuality.collectAsStateWithLifecycle()

        CollectionDetailsScreen(
            loadResult = loadResult,
            loggedInUsername = UserNickname(loggedInUsername),
            photosListLayoutType = photosLayoutType,
            photosLoadQuality = photosLoadQuality,
            onEvent = viewModel::onEvent,
            navigateBack = navController::navigateUp,
            navigateToPhotoDetails = navController::navigateToPhotoDetails,
            navigateToUserDetails = navController::navigateToUserDetails
        )
    }
}

fun NavController.navigateToCollectionDetails(collectionId: CollectionId) =
    this.navigate("${Screen.CollectionDetails.route}/${collectionId.value}")

object CollectionDetailsArgs {
    const val ID = "collectionId"
}