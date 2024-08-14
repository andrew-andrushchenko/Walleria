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
import com.andrii_a.walleria.ui.navigation.Screen
import com.andrii_a.walleria.ui.photo_details.navigateToPhotoDetails
import com.andrii_a.walleria.ui.user_details.navigateToUserDetails
import com.andrii_a.walleria.ui.util.collectAsOneTimeEvents

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

        val state by viewModel.state.collectAsStateWithLifecycle()

        viewModel.navigationEventsChannelFlow.collectAsOneTimeEvents { event ->
            when (event) {
                is CollectionDetailsNavigationEvent.NavigateBack -> {
                    navController.navigateUp()
                }

                is CollectionDetailsNavigationEvent.NavigateToPhotoDetails -> {
                    navController.navigateToPhotoDetails(event.photoId)
                }

                is CollectionDetailsNavigationEvent.NavigateToUserDetails -> {
                    navController.navigateToUserDetails(event.userNickname)
                }
            }
        }

        CollectionDetailsScreen(
            state = state,
            onEvent = viewModel::onEvent,
        )
    }
}

fun NavController.navigateToCollectionDetails(collectionId: CollectionId) =
    this.navigate("${Screen.CollectionDetails.route}/$collectionId")

object CollectionDetailsArgs {
    const val ID = "collectionId"
}