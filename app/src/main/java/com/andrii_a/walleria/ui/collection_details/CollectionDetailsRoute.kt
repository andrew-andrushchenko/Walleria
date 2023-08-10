package com.andrii_a.walleria.ui.collection_details

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
import com.andrii_a.walleria.ui.common.CollectionId
import com.andrii_a.walleria.ui.common.UserNickname
import com.andrii_a.walleria.ui.navigation.Screen
import com.andrii_a.walleria.ui.photo_details.navigateToPhotoDetails
import com.andrii_a.walleria.ui.user_details.navigateToUserDetails
import com.google.accompanist.systemuicontroller.SystemUiController

fun NavGraphBuilder.collectionDetailsRoute(
    navController: NavController,
    systemUiController: SystemUiController
) {
    composable(
        route = "${Screen.CollectionDetails.route}/{${CollectionDetailsArgs.ID}}",
        arguments = listOf(
            navArgument(CollectionDetailsArgs.ID) {
                type = NavType.StringType
                nullable = false
            }
        )
    ) { navBackStackEntry ->
        val systemBarsColors = Color.Transparent
        val isDark = isSystemInDarkTheme()

        LaunchedEffect(key1 = true) {
            systemUiController.setSystemBarsColor(
                color = systemBarsColors,
                darkIcons = !isDark
            )
        }

        val viewModel: CollectionDetailsViewModel = hiltViewModel()

        val collectionId = CollectionId(navBackStackEntry.arguments?.getString(CollectionDetailsArgs.ID).orEmpty())
        val loadResult by viewModel.loadResult.collectAsStateWithLifecycle()
        val loggedInUsername by viewModel.loggedInUsername.collectAsStateWithLifecycle()

        CollectionDetailsScreen(
            collectionId = collectionId,
            loadResult = loadResult,
            loggedInUsername = UserNickname(loggedInUsername),
            onEvent = viewModel::onEvent,
            navigateBack = navController::navigateUp,
            navigateToPhotoDetails = navController::navigateToPhotoDetails,
            navigateToUserDetails = navController::navigateToUserDetails
        )
    }
}

fun NavController.navigateToCollectionDetails(collectionId: CollectionId) =
    this.navigate("${Screen.CollectionDetails.route}/${collectionId.value}")

object CollectionDetailsArgs {
    const val ID = "collectionId"
}