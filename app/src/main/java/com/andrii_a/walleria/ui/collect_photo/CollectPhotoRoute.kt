package com.andrii_a.walleria.ui.collect_photo

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.andrii_a.walleria.ui.collect_photo.event.CollectPhotoNavigationEvent
import com.andrii_a.walleria.ui.navigation.Screen
import com.andrii_a.walleria.ui.util.InterScreenCommunicationKeys
import com.andrii_a.walleria.ui.util.collectAsOneTimeEvents

fun NavGraphBuilder.collectPhotoRoute(navController: NavController) {
    composable<Screen.CollectPhoto> {
        val viewModel: CollectPhotoViewModel = hiltViewModel()
        val state by viewModel.state.collectAsStateWithLifecycle()

        BackHandler(
            enabled = true,
            onBack = {
                navController.previousBackStackEntry
                    ?.savedStateHandle
                    ?.set(
                        InterScreenCommunicationKeys.COLLECT_SCREEN_RESULT_KEY,
                        state.isCollected
                    )

                navController.popBackStack()
            }
        )

        viewModel.navigationEventsChannelFlow.collectAsOneTimeEvents { event ->
            when (event) {
                CollectPhotoNavigationEvent.NavigateBack -> {
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set(
                            InterScreenCommunicationKeys.COLLECT_SCREEN_RESULT_KEY,
                            state.isCollected
                        )

                    navController.popBackStack()
                }
            }
        }

        CollectPhotoScreen(
            state = state,
            onEvent = viewModel::onEvent
        )
    }
}

