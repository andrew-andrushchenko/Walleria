package com.andrii_a.walleria.ui.user_details

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
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
import com.andrii_a.walleria.ui.common.UserNickname
import com.andrii_a.walleria.ui.navigation.Screen
import com.andrii_a.walleria.ui.photo_details.navigateToPhotoDetails
import com.andrii_a.walleria.ui.profile_edit.navigateToEditUserProfile
import com.andrii_a.walleria.ui.search.navigateToSearch
import com.google.accompanist.systemuicontroller.SystemUiController

fun NavGraphBuilder.userDetailsRoute(
    navController: NavController,
    systemUiController: SystemUiController
) {
    composable(
        route = "${Screen.UserDetails.route}/{${UserDetailsArgs.NICKNAME}}",
        arguments = listOf(
            navArgument(UserDetailsArgs.NICKNAME) {
                type = NavType.StringType
                nullable = false
            }
        )
    ) {
        val statusBarColor = MaterialTheme.colors.primary
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

        val viewModel: UserDetailsViewModel = hiltViewModel()

        val loadResult by viewModel.loadResult.collectAsStateWithLifecycle()

        UserDetailsScreen(
            loadResult = loadResult,
            onRetryLoading = viewModel::getUser,
            navigateBack = navController::navigateUp,
            navigateToPhotoDetails = navController::navigateToPhotoDetails,
            navigateToCollectionDetails = navController::navigateToCollectionDetails,
            navigateToEditUserProfile = navController::navigateToEditUserProfile,
            navigateToSearch = navController::navigateToSearch
        )
    }
}

fun NavController.navigateToUserDetails(userNickname: UserNickname) {
    this.navigate("${Screen.UserDetails.route}/${userNickname.value}")
}

object UserDetailsArgs {
    const val NICKNAME = "userNickname"
}