package com.andrii_a.walleria.ui.search

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.andrii_a.walleria.ui.collection_details.navigateToCollectionDetails
import com.andrii_a.walleria.ui.common.SearchQuery
import com.andrii_a.walleria.ui.navigation.Screen
import com.andrii_a.walleria.ui.photo_details.navigateToPhotoDetails
import com.andrii_a.walleria.ui.user_details.navigateToUserDetails
import com.andrii_a.walleria.ui.util.collectAsOneTimeEvents

fun NavGraphBuilder.searchRoute(navController: NavController) {
    composable(
        route = "${Screen.Search.route}?${SearchArgs.QUERY}={${SearchArgs.QUERY}}",
        arguments = listOf(
            navArgument(SearchArgs.QUERY) {
                type = NavType.StringType
                nullable = false
                defaultValue = ""
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
        val viewModel: SearchViewModel = hiltViewModel()

        val state by viewModel.state.collectAsStateWithLifecycle()

        viewModel.navigationEventsChannelFlow.collectAsOneTimeEvents { event ->
            when (event) {
                is SearchNavigationEvent.NavigateBack -> {
                    navController.navigateUp()
                }

                is SearchNavigationEvent.NavigateToPhotoDetails -> {
                    navController.navigateToPhotoDetails(event.photoId)
                }

                is SearchNavigationEvent.NavigateToCollectionDetails -> {
                    navController.navigateToCollectionDetails(event.collectionId)
                }

                is SearchNavigationEvent.NavigateToUserDetails -> {
                    navController.navigateToUserDetails(event.userNickname)
                }
            }
        }

        SearchScreen(
            state = state,
            onEvent = viewModel::onEvent
        )
    }
}

fun NavController.navigateToSearch(query: SearchQuery? = null) {
    val route = query?.let { "${Screen.Search.route}?${SearchArgs.QUERY}=${it.value}" }
        ?: Screen.Search.route
    this.navigate(route)
}

object SearchArgs {
    const val QUERY = "query"
}