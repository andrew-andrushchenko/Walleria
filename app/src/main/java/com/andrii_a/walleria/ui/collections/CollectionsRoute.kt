package com.andrii_a.walleria.ui.collections

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
import com.andrii_a.walleria.ui.collection_details.navigateToCollectionDetails
import com.andrii_a.walleria.ui.navigation.NavigationScreen
import com.andrii_a.walleria.ui.photo_details.navigateToPhotoDetails
import com.andrii_a.walleria.ui.search.navigateToSearch
import com.andrii_a.walleria.ui.user_details.navigateToUserDetails

fun NavGraphBuilder.collectionsBottomNavRoute(
    navController: NavController,
    openProfileBottomSheet: () -> Unit
) {
    composable(
        route = NavigationScreen.Collections.route,
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
        val viewModel: CollectionsViewModel = hiltViewModel()
        val collectionsLayoutType by viewModel.collectionsLayoutType.collectAsStateWithLifecycle()
        val photosLoadQuality by viewModel.photosLoadQuality.collectAsStateWithLifecycle()

        CollectionsScreen(
            collections = viewModel.collections,
            collectionsLayoutType = collectionsLayoutType,
            photosLoadQuality = photosLoadQuality,
            navigateToProfileScreen = openProfileBottomSheet,
            navigateToSearchScreen = navController::navigateToSearch,
            navigateToPhotoDetails = navController::navigateToPhotoDetails,
            navigateToCollectionDetails = navController::navigateToCollectionDetails,
            navigateToUserDetails = navController::navigateToUserDetails
        )
    }
}