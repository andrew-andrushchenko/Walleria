package com.andrii_a.walleria.ui.search

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.andrii_a.walleria.ui.navigation.NavigationScreen
import com.google.accompanist.systemuicontroller.SystemUiController

fun NavGraphBuilder.searchBottomNavRoute(
    navController: NavController,
    systemUiController: SystemUiController
) {
    composable(route = NavigationScreen.Search.route) {
        val statusBarColor = MaterialTheme.colors.primary.copy(alpha = 0.9f)
        val navigationBarColor = Color.Transparent
        val isDark = isSystemInDarkTheme()

        SideEffect {
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
        val dispatchEvent = viewModel::dispatchEvent

        SearchScreen(
            query = query,
            photos = photos,
            collections = collections,
            users = users,
            photoFilters = photoFilters,
            dispatchEvent = dispatchEvent
        )
    }
}