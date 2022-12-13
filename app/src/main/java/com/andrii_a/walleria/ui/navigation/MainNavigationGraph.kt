package com.andrii_a.walleria.ui.navigation

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.andrii_a.walleria.ui.collections.collectionsBottomNavRoute
import com.andrii_a.walleria.ui.photos.photosBottomNavRoute
import com.andrii_a.walleria.ui.topics.topicsBottomNavRoute
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
        photosBottomNavRoute(navHostController, systemUiController)
        collectionsBottomNavRoute(navHostController, systemUiController)
        topicsBottomNavRoute(navHostController, systemUiController)

        composable(route = NavigationScreen.Search.route) {
            val statusBarColor = MaterialTheme.colors.primary.copy(alpha = 0.95f)
            SideEffect {
                systemUiController.setStatusBarColor(
                    color = statusBarColor,
                    darkIcons = true
                )
                systemUiController.setNavigationBarColor(
                    color = Color.Transparent,
                    darkIcons = true
                )
            }
        }

        composable(route = NavigationScreen.Profile.route) {
            val statusBarColor = MaterialTheme.colors.primary.copy(alpha = 0.95f)
            SideEffect {
                systemUiController.setStatusBarColor(
                    color = statusBarColor,
                    darkIcons = true
                )
                systemUiController.setNavigationBarColor(
                    color = Color.Transparent,
                    darkIcons = true
                )
            }
        }
    }
}

private const val BottomNavigationGraphRoute = "walleria_bottom_navigation_graph"