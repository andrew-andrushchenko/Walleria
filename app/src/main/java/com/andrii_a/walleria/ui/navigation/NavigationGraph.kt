package com.andrii_a.walleria.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.andrii_a.walleria.ui.about.aboutRoute
import com.andrii_a.walleria.ui.collect_photo.collectPhotoRoute
import com.andrii_a.walleria.ui.collection_details.collectionDetailsRoute
import com.andrii_a.walleria.ui.photo_details.photoDetailsRoute
import com.andrii_a.walleria.ui.profile_edit.editUserProfileRoute
import com.andrii_a.walleria.ui.search.searchRoute
import com.andrii_a.walleria.ui.settings.settingsRoute
import com.andrii_a.walleria.ui.topic_details.topicDetailsRoute
import com.andrii_a.walleria.ui.user_details.userDetailsRoute

@Composable
fun AppNavigationHost(navHostController: NavHostController) {
    NavHost(
        navController = navHostController,
        startDestination = NavigationBarGraph
    ) {
        navigationBarGraph(navHostController)

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
