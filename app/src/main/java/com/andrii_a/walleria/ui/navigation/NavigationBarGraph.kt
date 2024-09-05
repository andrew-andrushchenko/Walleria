package com.andrii_a.walleria.ui.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.navigation
import com.andrii_a.walleria.ui.collections.collectionsBottomNavRoute
import com.andrii_a.walleria.ui.photos.photosBottomNavRoute
import com.andrii_a.walleria.ui.topics.topicsBottomNavRoute
import kotlinx.serialization.Serializable

@Serializable
object NavigationBarGraph

fun NavGraphBuilder.navigationBarGraph(
    navHostController: NavHostController,
    openProfileBottomSheet: () -> Unit
) {
    navigation<NavigationBarGraph>(startDestination = Screen.Photos) {
        photosBottomNavRoute(navHostController, openProfileBottomSheet)
        collectionsBottomNavRoute(navHostController, openProfileBottomSheet)
        topicsBottomNavRoute(navHostController, openProfileBottomSheet)
    }
}