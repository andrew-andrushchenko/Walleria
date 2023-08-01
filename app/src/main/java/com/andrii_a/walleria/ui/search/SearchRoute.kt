package com.andrii_a.walleria.ui.search

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.andrii_a.walleria.ui.collection_details.navigateToCollectionDetails
import com.andrii_a.walleria.ui.common.SearchQuery
import com.andrii_a.walleria.ui.navigation.Screen
import com.andrii_a.walleria.ui.photo_details.navigateToPhotoDetails
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
        )
    ) {
        val statusBarColor = MaterialTheme.colors.primary.copy(alpha = 0.9f)
        val navigationBarColor = Color.Transparent
        val isDark = isSystemInDarkTheme()

        LaunchedEffect(key1 = true) {
            systemUiController.setStatusBarColor(
                color = statusBarColor,
                darkIcons = !isDark
            )

            systemUiController.setNavigationBarColor(
                color = navigationBarColor,
                darkIcons = !isDark
            )
        }

        val viewModel: SearchViewModel = hiltViewModel()

        val query = viewModel.query
        val photos = viewModel.photos
        val collections = viewModel.collections
        val users = viewModel.users
        val photoFilters = viewModel.photoFilters

        SearchScreen(
            query = query,
            photos = photos,
            collections = collections,
            users = users,
            photoFilters = photoFilters,
            onEvent = viewModel::onEvent,
            navigateToPhotoDetails = navController::navigateToPhotoDetails,
            navigateToCollectionDetails = navController::navigateToCollectionDetails,
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