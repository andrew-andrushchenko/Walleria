package com.andrii_a.walleria.ui.collections

import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.andrii_a.walleria.ui.navigation.Screen
import com.andrii_a.walleria.ui.util.collectAsOneTimeEvents
import org.koin.androidx.compose.koinViewModel

fun NavGraphBuilder.collectionsNavigationBarRoute(navController: NavController) {
    composable<Screen.Collections> {
        val viewModel: CollectionsViewModel = koinViewModel()

        val state by viewModel.state.collectAsStateWithLifecycle()

        viewModel.navigationEventsChannelFlow.collectAsOneTimeEvents { event ->
            when (event) {
                is CollectionsNavigationEvent.NavigateToCollectionDetails -> {
                    navController.navigate(Screen.CollectionDetails(event.collectionId))
                }

                is CollectionsNavigationEvent.NavigateToPhotoDetailsScreen -> {
                    navController.navigate(Screen.PhotoDetails(event.photoId))
                }

                is CollectionsNavigationEvent.NavigateToSearchScreen -> {
                    navController.navigate(Screen.Search())
                }

                is CollectionsNavigationEvent.NavigateToUserDetails -> {
                    navController.navigate(Screen.UserDetails(event.userNickname))
                }
            }
        }

        CollectionsScreen(
            state = state,
            onEvent = viewModel::onEvent
        )
    }
}