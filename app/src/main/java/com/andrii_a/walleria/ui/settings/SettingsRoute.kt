package com.andrii_a.walleria.ui.settings

import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.andrii_a.walleria.ui.navigation.Screen

fun NavGraphBuilder.settingsRoute(navController: NavController) {
    composable<Screen.Settings> {
        val viewModel: SettingsViewModel = hiltViewModel()

        val photosLoadQuality by viewModel.photosLoadQuality.collectAsStateWithLifecycle()
        val photosDownloadQuality by viewModel.photosDownloadQuality.collectAsStateWithLifecycle()

        SettingsScreen(
            currentPhotosLoadQuality = photosLoadQuality,
            currentPhotosDownloadQuality = photosDownloadQuality,
            onEvent = viewModel::onEvent,
            navigateBack = navController::navigateUp
        )
    }
}
