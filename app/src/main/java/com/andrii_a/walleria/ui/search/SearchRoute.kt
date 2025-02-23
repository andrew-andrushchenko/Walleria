package com.andrii_a.walleria.ui.search

import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.andrii_a.walleria.ui.navigation.Screen
import com.andrii_a.walleria.ui.util.collectAsOneTimeEvents
import org.koin.compose.viewmodel.koinViewModel

fun NavGraphBuilder.searchRoute(navController: NavController) {
    composable<Screen.Search> {
        val viewModel: SearchViewModel = koinViewModel()

        val state by viewModel.state.collectAsStateWithLifecycle()

        viewModel.navigationEventsChannelFlow.collectAsOneTimeEvents { event ->
            when (event) {
                is SearchNavigationEvent.NavigateBack -> {
                    navController.navigateUp()
                }

                is SearchNavigationEvent.NavigateToPhotoDetails -> {
                    navController.navigate(Screen.PhotoDetails(event.photoId))
                }

                is SearchNavigationEvent.NavigateToCollectionDetails -> {
                    navController.navigate(Screen.CollectionDetails(event.collectionId))
                }

                is SearchNavigationEvent.NavigateToUserDetails -> {
                    navController.navigate(Screen.UserDetails(event.userNickname))
                }
            }
        }

        SearchScreen(
            state = state,
            onEvent = viewModel::onEvent
        )
    }
}
