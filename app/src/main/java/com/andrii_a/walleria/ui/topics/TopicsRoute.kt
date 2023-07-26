package com.andrii_a.walleria.ui.topics

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.andrii_a.walleria.ui.navigation.NavigationScreen
import com.andrii_a.walleria.ui.profile.navigateToProfileScreen
import com.andrii_a.walleria.ui.search.navigateToSearch
import com.google.accompanist.systemuicontroller.SystemUiController

fun NavGraphBuilder.topicsBottomNavRoute(
    navController: NavController,
    systemUiController: SystemUiController
) {
    composable(route = NavigationScreen.Topics.route) {
        val statusBarColor = MaterialTheme.colors.primary.copy(alpha = 0.9f)
        val navigationBarColor = Color.Transparent
        val darkIcons = !isSystemInDarkTheme()

        LaunchedEffect(key1 = true) {
            systemUiController.setStatusBarColor(
                color = statusBarColor,
                darkIcons = darkIcons
            )

            systemUiController.setNavigationBarColor(
                color = navigationBarColor,
                darkIcons = darkIcons
            )
        }

        val viewModel: TopicsViewModel = hiltViewModel()

        val topics = viewModel.topics
        val order by viewModel.order.collectAsStateWithLifecycle()
        val orderByFn = viewModel::orderBy

        TopicsScreen(
            topics = topics,
            order = order,
            orderBy = orderByFn,
            navigateToProfileScreen = navController::navigateToProfileScreen,
            navigateToSearchScreen = navController::navigateToSearch
        )
    }
}