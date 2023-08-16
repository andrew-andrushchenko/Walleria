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
        val systemBarsColor = Color.Transparent
        val areIconsDark = !isSystemInDarkTheme()

        LaunchedEffect(key1 = true) {
            systemUiController.setSystemBarsColor(
                color = systemBarsColor,
                darkIcons = areIconsDark
            )
        }

        val viewModel: SettingsViewModel = hiltViewModel()

        val photosListLayoutType by viewModel.photosListLayoutType.collectAsStateWithLifecycle()
        val collectionsListLayoutType by viewModel.collectionsListLayoutType.collectAsStateWithLifecycle()
        val photosLoadQuality by viewModel.photosLoadQuality.collectAsStateWithLifecycle()
        val photosDownloadQuality by viewModel.photosDownloadQuality.collectAsStateWithLifecycle()

        SettingsScreen(
            currentPhotosListLayoutType = photosListLayoutType,
            currentCollectionListLayoutType = collectionsListLayoutType,
            currentPhotosLoadQuality = photosLoadQuality,
            currentPhotosDownloadQuality = photosDownloadQuality,
            onEvent = viewModel::onEvent,
            navigateBack = navController::navigateUp
        )
    }
}

fun NavController.navigateToSettings() {
    this.navigate(Screen.Settings.route)
}