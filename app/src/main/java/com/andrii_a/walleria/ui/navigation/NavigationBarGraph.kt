package com.andrii_a.walleria.ui.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.navigation
import com.andrii_a.walleria.ui.collections.collectionsNavigationBarRoute
import com.andrii_a.walleria.ui.photos.photosNavigationBarRoute
import com.andrii_a.walleria.ui.topics.topicsNavigationBarRoute
import kotlinx.serialization.Serializable

@Serializable
object NavigationBarGraph

fun NavGraphBuilder.navigationBarGraph(
    navHostController: NavHostController,
    openProfileBottomSheet: () -> Unit
) {
    navigation<NavigationBarGraph>(startDestination = Screen.Photos) {
        photosNavigationBarRoute(navHostController, openProfileBottomSheet)
        collectionsNavigationBarRoute(navHostController, openProfileBottomSheet)
        topicsNavigationBarRoute(navHostController, openProfileBottomSheet)
    }
}