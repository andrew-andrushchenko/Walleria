package com.andrii_a.walleria.ui.navigation

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.navigation
import androidx.navigation.plusAssign
import com.andrii_a.walleria.ui.collect_photo.collectPhotoRoute
import com.andrii_a.walleria.ui.collections.collectionsBottomNavRoute
import com.andrii_a.walleria.ui.photo_details.photoDetailsRoute
import com.andrii_a.walleria.ui.photos.photosBottomNavRoute
import com.andrii_a.walleria.ui.profile.profileRoute
import com.andrii_a.walleria.ui.search.searchRoute
import com.andrii_a.walleria.ui.topics.topicsBottomNavRoute
import com.google.accompanist.navigation.material.BottomSheetNavigator
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.ModalBottomSheetLayout
import com.google.accompanist.systemuicontroller.SystemUiController

@OptIn(ExperimentalMaterialNavigationApi::class, ExperimentalMaterialApi::class)
@Composable
fun MainNavHost(
    navHostController: NavHostController,
    systemUiController: SystemUiController
) {
    val bottomSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = false
    )

    val bottomSheetNavigator = remember {
        BottomSheetNavigator(bottomSheetState)
    }

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

            searchRoute(navHostController, systemUiController)

            profileRoute(navHostController, systemUiController)

            photoDetailsRoute(navHostController, systemUiController)

            collectPhotoRoute(navHostController, systemUiController, bottomSheetState)
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
    }
}

private const val BottomNavigationGraphRoute = "walleria_bottom_navigation_graph"