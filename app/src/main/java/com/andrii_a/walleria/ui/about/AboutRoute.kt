package com.andrii_a.walleria.ui.about

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.andrii_a.walleria.ui.navigation.Screen
import com.andrii_a.walleria.ui.photo_details.navigateToPhotoDetails
import com.google.accompanist.systemuicontroller.SystemUiController

fun NavGraphBuilder.aboutRoute(
    navController: NavController,
    systemUiController: SystemUiController
) {
    composable(route = Screen.About.route) {
        val systemBarsColor = Color.Transparent
        val areIconsDark = !isSystemInDarkTheme()

        LaunchedEffect(key1 = true) {
            systemUiController.setSystemBarsColor(
                color = systemBarsColor,
                darkIcons = areIconsDark
            )
        }

        AboutScreen(
            navigateBack = navController::navigateUp,
            openPhoto = navController::navigateToPhotoDetails
        )
    }
}

fun NavController.navigateToAbout() {
    this.navigate(route = Screen.About.route)
}