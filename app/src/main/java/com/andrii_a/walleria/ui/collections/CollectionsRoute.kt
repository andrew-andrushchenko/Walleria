package com.andrii_a.walleria.ui.collections

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.andrii_a.walleria.ui.navigation.NavigationScreen
import com.andrii_a.walleria.ui.profile.navigateToProfileScreen
import com.google.accompanist.systemuicontroller.SystemUiController

fun NavGraphBuilder.collectionsBottomNavRoute(
    navController: NavController,
    systemUiController: SystemUiController
) {
    composable(route = NavigationScreen.Collections.route) {
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

        val viewModel: CollectionsViewModel = hiltViewModel()

        CollectionsScreen(
            collections = viewModel.collections,
            navigateToProfileScreen = {
                navController.navigateToProfileScreen()
            }
        )
    }
}