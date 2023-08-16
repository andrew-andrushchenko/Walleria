package com.andrii_a.walleria.ui.profile_edit

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.andrii_a.walleria.ui.navigation.Screen
import com.andrii_a.walleria.ui.util.toast
import com.google.accompanist.systemuicontroller.SystemUiController

fun NavGraphBuilder.editUserProfileRoute(
    navController: NavController,
    systemUiController: SystemUiController
) {
    composable(route = Screen.EditUserProfile.route) {
        val systemBarsColor = Color.Transparent
        val areIconsDark = !isSystemInDarkTheme()

        LaunchedEffect(key1 = true) {
            systemUiController.setSystemBarsColor(
                color = systemBarsColor,
                darkIcons = areIconsDark
            )
        }

        val viewModel: EditUserProfileViewModel = hiltViewModel()

        val state by viewModel.state.collectAsStateWithLifecycle()

        val context = LocalContext.current
        LaunchedEffect(key1 = true) {
            viewModel.profileUpdateMessageFlow.collect { message ->
                context.toast(message.asString(context))
            }
        }

        EditUserProfileScreen(
            state = state,
            onEvent = viewModel::onEvent,
            onNavigateBack = navController::navigateUp
        )
    }
}

fun NavController.navigateToEditUserProfile() {
    this.navigate(Screen.EditUserProfile.route)
}