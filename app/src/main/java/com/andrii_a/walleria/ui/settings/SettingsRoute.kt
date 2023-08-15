package com.andrii_a.walleria.ui.settings

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.andrii_a.walleria.ui.navigation.Screen
import com.google.accompanist.systemuicontroller.SystemUiController

fun NavGraphBuilder.settingsRoute(
    navController: NavController,
    systemUiController: SystemUiController
) {
    composable(route = Screen.Settings.route) {
        val systemBarsColors = Color.Transparent
        val isDark = isSystemInDarkTheme()

        LaunchedEffect(key1 = true) {
            systemUiController.setSystemBarsColor(
                color = systemBarsColors,
                darkIcons = !isDark
            )
        }

        val viewModel: SettingsViewModel = hiltViewModel()

        val photosListLayoutType by viewModel.photosListLayoutType.collectAsStateWithLifecycle()
        val collectionsListLayoutType by viewModel.collectionsListLayoutType.collectAsStateWithLifecycle()
        val photoPreviewsQuality by viewModel.photosLoadQuality.collectAsStateWithLifecycle()

        SettingsScreen(
            currentPhotosListLayoutType = photosListLayoutType,
            currentCollectionListLayoutType = collectionsListLayoutType,
            currentPhotoPreviewsQuality = photoPreviewsQuality,
            onEvent = viewModel::onEvent,
            navigateBack = navController::navigateUp
        )
    }
}

fun NavController.navigateToSettings() {
    this.navigate(Screen.Settings.route)
}