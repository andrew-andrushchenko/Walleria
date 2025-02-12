package com.andrii_a.walleria.ui.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.navigation
import com.andrii_a.walleria.ui.account.accountNavigationRoute
import com.andrii_a.walleria.ui.collections.collectionsNavigationBarRoute
import com.andrii_a.walleria.ui.photos.photosNavigationBarRoute
import com.andrii_a.walleria.ui.topics.topicsNavigationBarRoute
import kotlinx.serialization.Serializable

@Serializable
object NavigationBarGraph

fun NavGraphBuilder.navigationBarGraph(
    navHostController: NavHostController
) {
    navigation<NavigationBarGraph>(startDestination = Screen.Photos) {
        photosNavigationBarRoute(navHostController)
        collectionsNavigationBarRoute(navHostController)
        topicsNavigationBarRoute(navHostController)
        accountNavigationRoute(navHostController)
    }
}