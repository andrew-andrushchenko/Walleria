package com.andrii_a.walleria.ui.navigation

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.navigation
import androidx.navigation.plusAssign
import com.andrii_a.walleria.ui.collections.collectionsBottomNavRoute
import com.andrii_a.walleria.ui.photo_details.photoDetailsRoute
import com.andrii_a.walleria.ui.photos.photosBottomNavRoute
import com.andrii_a.walleria.ui.profile.profileRoute
import com.andrii_a.walleria.ui.search.searchBottomNavRoute
import com.andrii_a.walleria.ui.topics.topicsBottomNavRoute
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.ModalBottomSheetLayout
import com.google.accompanist.navigation.material.rememberBottomSheetNavigator
import com.google.accompanist.systemuicontroller.SystemUiController

@OptIn(ExperimentalMaterialNavigationApi::class)
@Composable
fun MainNavHost(
    navHostController: NavHostController,
    systemUiController: SystemUiController
) {
    val bottomSheetNavigator = rememberBottomSheetNavigator()

    navHostController.navigatorProvider += bottomSheetNavigator

    ModalBottomSheetLayout(
        bottomSheetNavigator = bottomSheetNavigator,
        scrimColor = MaterialTheme.colors.primary.copy(alpha = 0.5f)
    ) {
        NavHost(
            navController = navHostController,
            startDestination = BottomNavigationGraphRoute
        ) {
            bottomNavigation(navHostController, systemUiController)

            profileRoute(navHostController, systemUiController)

            photoDetailsRoute(navHostController, systemUiController)
        }
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