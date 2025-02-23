package com.andrii_a.walleria.ui.profile_edit

import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.andrii_a.walleria.ui.navigation.Screen
import com.andrii_a.walleria.ui.util.collectAsOneTimeEvents
import com.andrii_a.walleria.ui.util.toast
import org.koin.androidx.compose.koinViewModel

fun NavGraphBuilder.editUserProfileRoute(navController: NavController) {
    composable<Screen.EditUserProfile> {
        val viewModel: EditUserProfileViewModel = koinViewModel()

        val state by viewModel.state.collectAsStateWithLifecycle()

        val context = LocalContext.current
        viewModel.profileUpdateMessageFlow.collectAsOneTimeEvents { uiText ->
            context.toast(uiText.asString(context))
        }

        viewModel.navigationEventsChannelFlow.collectAsOneTimeEvents { event ->
            when (event) {
                is EditUserProfileNavigationEvent.NavigateBack -> {
                    navController.navigateUp()
                }
            }
        }

        EditUserProfileScreen(
            state = state,
            onEvent = viewModel::onEvent
        )
    }
}
