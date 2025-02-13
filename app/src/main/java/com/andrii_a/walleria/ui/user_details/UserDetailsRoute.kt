package com.andrii_a.walleria.ui.user_details

import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.andrii_a.walleria.ui.navigation.Screen
import com.andrii_a.walleria.ui.util.collectAsOneTimeEvents
import com.andrii_a.walleria.ui.util.openInstagramProfile
import com.andrii_a.walleria.ui.util.openLinkInBrowser
import com.andrii_a.walleria.ui.util.openTwitterProfile
import com.andrii_a.walleria.ui.util.openUserProfileInBrowser

fun NavGraphBuilder.userDetailsRoute(navController: NavController) {
    composable<Screen.UserDetails> {
        val viewModel: UserDetailsViewModel = hiltViewModel()

        val state by viewModel.state.collectAsStateWithLifecycle()

        val context = LocalContext.current
        viewModel.navigationEventsChannelFlow.collectAsOneTimeEvents { event ->
            when (event) {
                is UserDetailsNavigationEvent.NavigateBack -> {
                    navController.navigateUp()
                }

                is UserDetailsNavigationEvent.NavigateToPhotoDetailsScreen -> {
                    navController.navigate(Screen.PhotoDetails(event.photoId))
                }

                is UserDetailsNavigationEvent.NavigateToCollectionDetails -> {
                    navController.navigate(Screen.CollectionDetails(event.collectionId))
                }

                is UserDetailsNavigationEvent.NavigateToUserDetails -> {
                    navController.navigate(Screen.UserDetails(event.userNickname))
                }

                is UserDetailsNavigationEvent.NavigateToSearchScreen -> {
                    navController.navigate(Screen.Search(event.query))
                }

                is UserDetailsNavigationEvent.NavigateToEditProfile -> {
                    navController.navigate(Screen.EditUserProfile)
                }

                is UserDetailsNavigationEvent.NavigateToUserProfileInChromeTab -> {
                    context.openUserProfileInBrowser(event.userNickname)
                }

                is UserDetailsNavigationEvent.NavigateToChromeCustomTab -> {
                    context.openLinkInBrowser(event.url)
                }

                is UserDetailsNavigationEvent.NavigateToInstagramApp -> {
                    context.openInstagramProfile(event.instagramNickname)
                }

                is UserDetailsNavigationEvent.NavigateToTwitterApp -> {
                    context.openTwitterProfile(event.twitterNickname)
                }
            }
        }

        UserDetailsScreen(
            state = state,
            onEvent = viewModel::onEvent
        )
    }
}
