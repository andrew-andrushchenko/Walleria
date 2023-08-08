package com.andrii_a.walleria.ui.profile

import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import com.andrii_a.walleria.ui.login.LoginActivity
import com.andrii_a.walleria.ui.navigation.Screen
import com.andrii_a.walleria.ui.util.startActivity
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.bottomSheet
import com.google.accompanist.systemuicontroller.SystemUiController

@OptIn(ExperimentalMaterialNavigationApi::class)
fun NavGraphBuilder.profileRoute(
    navController: NavController,
    systemUiController: SystemUiController
) {
    bottomSheet(route = Screen.Profile.route) {
        val context = LocalContext.current

        val viewModel: ProfileViewModel = hiltViewModel()

        val isUserLoggedIn by viewModel.isUserLoggedIn.collectAsStateWithLifecycle()
        val userProfileData by viewModel.myProfileData.collectAsStateWithLifecycle()

        ProfileScreen(
            isUserLoggedIn = isUserLoggedIn,
            userProfileData = userProfileData,
            navigateToLoginScreen = {
                context.startActivity(LoginActivity::class.java)
            },
            logout = viewModel::logout,
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