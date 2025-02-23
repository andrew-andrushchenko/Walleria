package com.andrii_a.walleria.ui.collection_details

import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.andrii_a.walleria.ui.navigation.Screen
import com.andrii_a.walleria.ui.util.collectAsOneTimeEvents
import org.koin.androidx.compose.koinViewModel

fun NavGraphBuilder.collectionDetailsRoute(navController: NavController) {
    composable<Screen.CollectionDetails> {
        val viewModel: CollectionDetailsViewModel = koinViewModel()

        val state by viewModel.state.collectAsStateWithLifecycle()

        viewModel.navigationEventsChannelFlow.collectAsOneTimeEvents { event ->
            when (event) {
                is CollectionDetailsNavigationEvent.NavigateBack -> {
                    navController.navigateUp()
                }

                is CollectionDetailsNavigationEvent.NavigateToPhotoDetails -> {
                    navController.navigate(Screen.PhotoDetails(event.photoId))
                }

                is CollectionDetailsNavigationEvent.NavigateToUserDetails -> {
                    navController.navigate(Screen.UserDetails(event.userNickname))
                }
            }
        }

        CollectionDetailsScreen(
            state = state,
            onEvent = viewModel::onEvent,
        )
    }
}
