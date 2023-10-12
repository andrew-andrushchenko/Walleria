package com.andrii_a.walleria.ui.search

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.andrii_a.walleria.ui.collection_details.navigateToCollectionDetails
import com.andrii_a.walleria.ui.common.SearchQuery
import com.andrii_a.walleria.ui.navigation.Screen
import com.andrii_a.walleria.ui.photo_details.navigateToPhotoDetails
import com.andrii_a.walleria.ui.user_details.navigateToUserDetails
import com.google.accompanist.systemuicontroller.SystemUiController

fun NavGraphBuilder.searchRoute(
    navController: NavController,
    systemUiController: SystemUiController
) {
    composable(
        route = "${Screen.Search.route}?${SearchArgs.QUERY}={${SearchArgs.QUERY}}",
        arguments = listOf(
            navArgument(SearchArgs.QUERY) {
                type = NavType.StringType
                nullable = false
                defaultValue = ""
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
        val systemBarsColor = Color.Transparent
        val areIconsDark = !isSystemInDarkTheme()

        LaunchedEffect(key1 = true) {
            systemUiController.setSystemBarsColor(
                color = systemBarsColor,
                darkIcons = areIconsDark
            )
        }

        val viewModel: SearchViewModel = hiltViewModel()

        val query by viewModel.query.collectAsStateWithLifecycle()
        val recentSearches by viewModel.recentSearches.collectAsStateWithLifecycle()

        val photosLayoutType by viewModel.photosLayoutType.collectAsStateWithLifecycle()
        val collectionsLayoutType by viewModel.collectionsLayoutType.collectAsStateWithLifecycle()
        val photosLoadQuality by viewModel.photosLoadQuality.collectAsStateWithLifecycle()

        SearchScreen(
            query = query,
            recentSearches = recentSearches,
            photos = viewModel.photos,
            collections = viewModel.collections,
            users = viewModel.users,
            photoFilters = viewModel.photoFilters,
            photosListLayoutType = photosLayoutType,
            collectionListLayoutType = collectionsLayoutType,
            photosLoadQuality = photosLoadQuality,
            onEvent = viewModel::onEvent,
            navigateToPhotoDetails = navController::navigateToPhotoDetails,
            navigateToCollectionDetails = navController::navigateToCollectionDetails,
            navigateToUserDetails = navController::navigateToUserDetails,
            navigateBack = navController::navigateUp
        )
    }
}

fun NavController.navigateToSearch(query: SearchQuery? = null) {
    val route = query?.let { "${Screen.Search.route}?${SearchArgs.QUERY}=${it.value}" }
        ?: Screen.Search.route
    this.navigate(route)
}

object SearchArgs {
    const val QUERY = "query"
}