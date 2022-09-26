package com.andrii_a.walleria.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.navigation

@Composable
fun MainNavHost(navHostController: NavHostController) {
    NavHost(
        navController = navHostController,
        startDestination = BottomNavigationGraphRoute
    ) {
        bottomNavigation(navHostController)
    }
}

fun NavGraphBuilder.bottomNavigation(navHostController: NavHostController) {
    navigation(
        route = BottomNavigationGraphRoute,
        startDestination = NavigationScreen.Photos.route
    ) {

    }
}

private const val BottomNavigationGraphRoute = "walleria_bottom_navigation_graph"