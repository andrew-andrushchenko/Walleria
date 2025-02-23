package com.andrii_a.walleria.ui.photos

import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.andrii_a.walleria.ui.navigation.Screen
import com.andrii_a.walleria.ui.util.collectAsOneTimeEvents
import org.koin.compose.viewmodel.koinViewModel

fun NavGraphBuilder.photosNavigationBarRoute(
    navController: NavController
) {
    composable<Screen.Photos> {
        val viewModel: PhotosViewModel = koinViewModel()

        val state by viewModel.state.collectAsStateWithLifecycle()

        viewModel.navigationEventsChannelFlow.collectAsOneTimeEvents { event ->
            when (event) {
                is PhotosNavigationEvent.NavigateToPhotoDetailsScreen -> {
                    navController.navigate(Screen.PhotoDetails(event.photoId))
                }

                is PhotosNavigationEvent.NavigateToUserDetails -> {
                    navController.navigate(Screen.UserDetails(event.userNickname))
                }

                is PhotosNavigationEvent.NavigateToSearchScreen -> {
                    navController.navigate(Screen.Search())
                }
            }
        }

        PhotosScreen(
            state = state,
            onEvent = viewModel::onEvent
        )
    }
}