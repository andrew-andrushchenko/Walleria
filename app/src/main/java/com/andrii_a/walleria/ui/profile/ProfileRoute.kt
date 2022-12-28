package com.andrii_a.walleria.ui.profile

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.dialog
import com.andrii_a.walleria.ui.navigation.Screen
import com.google.accompanist.systemuicontroller.SystemUiController

fun NavGraphBuilder.profileRoute(
    navController: NavController,
    systemUiController: SystemUiController
) {
    dialog(route = Screen.Profile.route) {

        val viewModel: ProfileViewModel = hiltViewModel()

        val isUserLoggedInState = viewModel.isUserLoggedIn
        val userProfileData = viewModel.myProfileData

        val logout = viewModel::logout

        ProfileScreen(
            isUserLoggedInStateFlow = isUserLoggedInState,
            userProfileDataStateFlow = userProfileData,
            navigateToLoginScreen = {},
            logout = logout,
            navigateToViewProfileScreen = {},
            navigateToEditProfileScreen = {},
            navigateToSettingsScreen = {},
            navigateToAboutScreen = {}
        )
    }
}

fun NavController.navigateToProfileScreen() {
    this.navigate(Screen.Profile.route)
}