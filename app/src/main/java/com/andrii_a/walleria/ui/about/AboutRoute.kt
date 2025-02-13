package com.andrii_a.walleria.ui.about

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.andrii_a.walleria.ui.navigation.Screen

fun NavGraphBuilder.aboutRoute(navController: NavController) {
    composable<Screen.About> {
        AboutScreen(
            navigateBack = navController::navigateUp,
        )
    }
}

