package com.andrii_a.walleria.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.navigation
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

    }
}

private const val BottomNavigationGraphRoute = "walleria_bottom_navigation_graph"