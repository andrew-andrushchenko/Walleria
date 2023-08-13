package com.andrii_a.walleria.ui.navigation

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.navigation
import androidx.navigation.plusAssign
import com.andrii_a.walleria.ui.collect_photo.collectPhotoRoute
import com.andrii_a.walleria.ui.collection_details.collectionDetailsRoute
import com.andrii_a.walleria.ui.collections.collectionsBottomNavRoute
import com.andrii_a.walleria.ui.photo_details.photoDetailsRoute
import com.andrii_a.walleria.ui.photos.photosBottomNavRoute
import com.andrii_a.walleria.ui.profile.profileRoute
import com.andrii_a.walleria.ui.profile_edit.editUserProfileRoute
import com.andrii_a.walleria.ui.search.searchRoute
import com.andrii_a.walleria.ui.topic_details.topicDetailsRoute
import com.andrii_a.walleria.ui.topics.topicsBottomNavRoute
import com.andrii_a.walleria.ui.user_details.userDetailsRoute
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
        scrimColor = MaterialTheme.colors.primary.copy(alpha = 0.5f),
        sheetShape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        NavHost(
            navController = navHostController,
            startDestination = NAVIGATION_BAR_GRAPH_ROUTE
        ) {
            bottomNavigation(navHostController, systemUiController)

            searchRoute(navHostController, systemUiController)

            profileRoute(navHostController)

            editUserProfileRoute(navHostController, systemUiController)

            photoDetailsRoute(navHostController, systemUiController)

            collectPhotoRoute(navHostController, bottomSheetState)

            collectionDetailsRoute(navHostController, systemUiController)

            userDetailsRoute(navHostController, systemUiController)

            topicDetailsRoute(navHostController, systemUiController)
        }
    }

}

fun NavGraphBuilder.bottomNavigation(
    navHostController: NavHostController,
    systemUiController: SystemUiController
) {
    navigation(
        route = NAVIGATION_BAR_GRAPH_ROUTE,
        startDestination = NavigationScreen.Photos.route
    ) {
        photosBottomNavRoute(navHostController, systemUiController)
        collectionsBottomNavRoute(navHostController, systemUiController)
        topicsBottomNavRoute(navHostController, systemUiController)
    }
}

private const val NAVIGATION_BAR_GRAPH_ROUTE = "walleria_navigation_bar_graph_route"