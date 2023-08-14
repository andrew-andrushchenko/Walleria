package com.andrii_a.walleria.ui.topic_details

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
import com.andrii_a.walleria.ui.common.TopicId
import com.andrii_a.walleria.ui.navigation.Screen
import com.andrii_a.walleria.ui.photo_details.navigateToPhotoDetails
import com.andrii_a.walleria.ui.user_details.navigateToUserDetails
import com.google.accompanist.systemuicontroller.SystemUiController

fun NavGraphBuilder.topicDetailsRoute(
    navController: NavController,
    systemUiController: SystemUiController
) {
    composable(
        route = "${Screen.TopicDetails.route}/{${TopicDetailsArgs.ID}}",
        arguments = listOf(
            navArgument(TopicDetailsArgs.ID) {
                type = NavType.StringType
                nullable = false
            }
        )
    ) {
        val systemBarsColors = Color.Transparent
        val isDark = isSystemInDarkTheme()

        LaunchedEffect(key1 = true) {
            systemUiController.setSystemBarsColor(
                color = systemBarsColors,
                darkIcons = !isDark
            )
        }

        val viewModel: TopicDetailsViewModel = hiltViewModel()

        val loadResult by viewModel.loadResult.collectAsStateWithLifecycle()
        val photosLayoutType by viewModel.photosLayoutType.collectAsStateWithLifecycle()
        val photosLoadQuality by viewModel.photosLoadQuality.collectAsStateWithLifecycle()

        TopicDetailsScreen(
            loadResult = loadResult,
            photosListLayoutType = photosLayoutType,
            photosLoadQuality = photosLoadQuality,
            onEvent = viewModel::onEvent,
            navigateBack = navController::navigateUp,
            navigateToPhotoDetails = navController::navigateToPhotoDetails,
            navigateToUserDetails = navController::navigateToUserDetails
        )
    }
}

fun NavController.navigateToTopicDetails(topicId: TopicId) {
    this.navigate("${Screen.TopicDetails.route}/${topicId.value}")
}

object TopicDetailsArgs {
    const val ID = "topicId"
}