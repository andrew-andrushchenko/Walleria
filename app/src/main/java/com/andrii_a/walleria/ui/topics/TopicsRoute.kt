package com.andrii_a.walleria.ui.topics

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.andrii_a.walleria.ui.navigation.NavigationScreen
import com.andrii_a.walleria.ui.search.navigateToSearch
import com.andrii_a.walleria.ui.topic_details.navigateToTopicDetails
import com.google.accompanist.systemuicontroller.SystemUiController

fun NavGraphBuilder.topicsBottomNavRoute(
    navController: NavController,
    systemUiController: SystemUiController,
    openProfileBottomSheet: () -> Unit
) {
    composable(route = NavigationScreen.Topics.route) {
        val systemBarsColor = Color.Transparent
        val areIconsDark = !isSystemInDarkTheme()

        LaunchedEffect(key1 = true) {
            systemUiController.setSystemBarsColor(
                color = systemBarsColor,
                darkIcons = areIconsDark
            )
        }

        val viewModel: TopicsViewModel = hiltViewModel()
        val order by viewModel.order.collectAsStateWithLifecycle()

        TopicsScreen(
            topics = viewModel.topics,
            order = order,
            orderBy = viewModel::orderBy,
            navigateToTopicDetails = navController::navigateToTopicDetails,
            navigateToProfileScreen = openProfileBottomSheet,
            navigateToSearchScreen = navController::navigateToSearch
        )
    }
}