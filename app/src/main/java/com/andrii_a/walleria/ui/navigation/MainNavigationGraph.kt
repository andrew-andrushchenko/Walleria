package com.andrii_a.walleria.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.navigation
import com.andrii_a.walleria.ui.collections.collectionsBottomNavRoute
import com.andrii_a.walleria.ui.photos.photosBottomNavRoute
import com.andrii_a.walleria.ui.search.searchBottomNavRoute
import com.andrii_a.walleria.ui.topics.topicsBottomNavRoute
import com.andrii_a.walleria.ui.profile.profileRoute
import com.google.accompanist.systemuicontroller.SystemUiController

@Composable
fun MainNavHost(
    navHostController: NavHostController,
    systemUiController: SystemUiController
) {
    NavHost(
        navController = navHostController,
        startDestination = BottomNavigationGraphRoute
    ) {
        bottomNavigation(navHostController, systemUiController)

        profileRoute(navHostController, systemUiController)
    }
}

fun NavGraphBuilder.bottomNavigation(
    navHostController: NavHostController,
    systemUiController: SystemUiController
) {
    navigation(
        route = BottomNavigationGraphRoute,
        startDestination = NavigationScreen.Photos.route
    ) {
        photosBottomNavRoute(navHostController, systemUiController)
        collectionsBottomNavRoute(navHostController, systemUiController)
        topicsBottomNavRoute(navHostController, systemUiController)
        searchBottomNavRoute(navHostController, systemUiController)
    }
}

private const val BottomNavigationGraphRoute = "walleria_bottom_navigation_graph"