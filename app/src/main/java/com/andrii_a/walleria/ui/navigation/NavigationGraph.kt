package com.andrii_a.walleria.ui.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.scaleOut
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.TransformOrigin
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.andrii_a.walleria.ui.about.aboutRoute
import com.andrii_a.walleria.ui.collect_photo.collectPhotoRoute
import com.andrii_a.walleria.ui.collection_details.collectionDetailsRoute
import com.andrii_a.walleria.ui.login.loginRoute
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
        startDestination = NavigationBarGraph,
        popExitTransition = {
            scaleOut(
                targetScale = 0.85f,
                transformOrigin = TransformOrigin(pivotFractionX = 0.5f, pivotFractionY = 0.5f)
            )
        },
        popEnterTransition = {
            EnterTransition.None
        },
    ) {
        navigationBarGraph(navHostController)

        loginRoute(navHostController)

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
