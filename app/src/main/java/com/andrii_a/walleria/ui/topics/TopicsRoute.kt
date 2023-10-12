package com.andrii_a.walleria.ui.topics

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
    composable(
        route = NavigationScreen.Topics.route,
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