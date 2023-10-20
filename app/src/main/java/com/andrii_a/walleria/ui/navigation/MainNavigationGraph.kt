package com.andrii_a.walleria.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.navigation
import com.andrii_a.walleria.ui.about.aboutRoute
import com.andrii_a.walleria.ui.collect_photo.collectPhotoRoute
import com.andrii_a.walleria.ui.collection_details.collectionDetailsRoute
import com.andrii_a.walleria.ui.collections.collectionsBottomNavRoute
import com.andrii_a.walleria.ui.photo_details.photoDetailsRoute
import com.andrii_a.walleria.ui.photos.photosBottomNavRoute
import com.andrii_a.walleria.ui.profile_edit.editUserProfileRoute
import com.andrii_a.walleria.ui.search.searchRoute
import com.andrii_a.walleria.ui.settings.settingsRoute
import com.andrii_a.walleria.ui.topic_details.topicDetailsRoute
import com.andrii_a.walleria.ui.topics.topicsBottomNavRoute
import com.andrii_a.walleria.ui.user_details.userDetailsRoute

@Composable
fun MainNavigationHost(
    navHostController: NavHostController,
    openProfileBottomSheet: () -> Unit
) {
    NavHost(
        navController = navHostController,
        startDestination = NAVIGATION_BAR_GRAPH_ROUTE
    ) {
        navigationBarGraph(navHostController, openProfileBottomSheet)

        searchRoute(navHostController)

        editUserProfileRoute(navHostController)

        photoDetailsRoute(navHostController)

        collectPhotoRoute(navHostController)

        collectionDetailsRoute(navHostController)

        userDetailsRoute(navHostController)

        topicDetailsRoute(navHostController)

        settingsRoute(navHostController)

        aboutRoute(navHostController)
    }

}

fun NavGraphBuilder.navigationBarGraph(
    navHostController: NavHostController,
    openProfileBottomSheet: () -> Unit
) {
    navigation(
        route = NAVIGATION_BAR_GRAPH_ROUTE,
        startDestination = NavigationScreen.Photos.route
    ) {
        photosBottomNavRoute(navHostController, openProfileBottomSheet)
        collectionsBottomNavRoute(navHostController, openProfileBottomSheet)
        topicsBottomNavRoute(navHostController, openProfileBottomSheet)
    }
}

private const val NAVIGATION_BAR_GRAPH_ROUTE = "walleria_navigation_bar_graph_route"