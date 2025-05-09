package com.andrii_a.walleria.ui.settings

import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.andrii_a.walleria.ui.navigation.Screen
import com.andrii_a.walleria.ui.util.collectAsOneTimeEvents
import org.koin.androidx.compose.koinViewModel

fun NavGraphBuilder.settingsRoute(navController: NavController) {
    composable<Screen.Settings> {
        val viewModel: SettingsViewModel = koinViewModel()

        viewModel.navigationEventFlow.collectAsOneTimeEvents { event ->
            when (event) {
                is SettingsNavigationEvent.NavigateBack -> {
                    navController.navigateUp()
                }
            }
        }

        val state by viewModel.state.collectAsStateWithLifecycle()

        SettingsScreen(
            state = state,
            onEvent = viewModel::onEvent,
        )
    }
}
