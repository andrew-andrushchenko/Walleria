package com.andrii_a.walleria.ui.photos

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.andrii_a.walleria.ui.navigation.NavigationScreen
import com.google.accompanist.systemuicontroller.SystemUiController

fun NavGraphBuilder.photosBottomNavRoute(
    navController: NavController,
    systemUiController: SystemUiController
) {
    composable(route = NavigationScreen.Photos.route) {
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

        val viewModel: PhotosViewModel = hiltViewModel()

        val photos = viewModel.photos
        val order by viewModel.order.collectAsState()
        val orderByFun = viewModel::orderBy

        PhotosScreen(photos = photos, order = order, orderBy = orderByFun)
    }
}