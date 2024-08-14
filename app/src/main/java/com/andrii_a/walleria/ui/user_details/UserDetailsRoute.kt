package com.andrii_a.walleria.ui.user_details

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.andrii_a.walleria.ui.collection_details.navigateToCollectionDetails
import com.andrii_a.walleria.ui.common.UserNickname
import com.andrii_a.walleria.ui.navigation.Screen
import com.andrii_a.walleria.ui.photo_details.navigateToPhotoDetails
import com.andrii_a.walleria.ui.profile_edit.navigateToEditUserProfile
import com.andrii_a.walleria.ui.search.navigateToSearch
import com.andrii_a.walleria.ui.util.collectAsOneTimeEvents
import com.andrii_a.walleria.ui.util.openInstagramProfile
import com.andrii_a.walleria.ui.util.openLinkInBrowser
import com.andrii_a.walleria.ui.util.openTwitterProfile
import com.andrii_a.walleria.ui.util.openUserProfileInBrowser

fun NavGraphBuilder.userDetailsRoute(navController: NavController) {
    composable(
        route = "${Screen.UserDetails.route}/{${UserDetailsArgs.NICKNAME}}",
        arguments = listOf(
            navArgument(UserDetailsArgs.NICKNAME) {
                type = NavType.StringType
                nullable = false
            }
        ),
        enterTransition = {
            fadeIn(
                animationSpec = tween(300, easing = LinearEasing)
            ) + slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Start,
                animationSpec = spring(stiffness = Spring.StiffnessMedium)
            )
        },
        popEnterTransition = {
            fadeIn(
                animationSpec = tween(300, easing = LinearEasing)
            ) + slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.End,
                animationSpec = spring(stiffness = Spring.StiffnessMedium)
            )
        }
    ) {
        val viewModel: UserDetailsViewModel = hiltViewModel()

        val state by viewModel.state.collectAsStateWithLifecycle()

        val context = LocalContext.current
        viewModel.navigationEventsChannelFlow.collectAsOneTimeEvents { event ->
            when (event) {
                is UserDetailsNavigationEvent.NavigateBack -> {
                    navController.navigateUp()
                }

                is UserDetailsNavigationEvent.NavigateToPhotoDetailsScreen -> {
                    navController.navigateToPhotoDetails(event.photoId)
                }

                is UserDetailsNavigationEvent.NavigateToCollectionDetails -> {
                    navController.navigateToCollectionDetails(event.collectionId)
                }

                is UserDetailsNavigationEvent.NavigateToUserDetails -> {
                    navController.navigateToUserDetails(event.userNickname)
                }

                is UserDetailsNavigationEvent.NavigateToSearchScreen -> {
                    navController.navigateToSearch(event.query)
                }

                is UserDetailsNavigationEvent.NavigateToEditProfile -> {
                    navController.navigateToEditUserProfile()
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

fun NavController.navigateToUserDetails(userNickname: UserNickname) {
    this.navigate("${Screen.UserDetails.route}/$userNickname")
}

object UserDetailsArgs {
    const val NICKNAME = "userNickname"
}