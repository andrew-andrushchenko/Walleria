package com.andrii_a.walleria.ui.collect_photo

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.andrii_a.walleria.ui.common.PhotoId
import com.andrii_a.walleria.ui.navigation.Screen
import com.andrii_a.walleria.ui.util.InterScreenCommunicationKeys
import com.andrii_a.walleria.ui.util.toast
import com.google.accompanist.systemuicontroller.SystemUiController

fun NavGraphBuilder.collectPhotoRoute(
    navController: NavController,
    systemUiController: SystemUiController
) {
    composable(
        route = "${Screen.CollectPhoto.route}/{${CollectPhotoArgs.PHOTO_ID}}",
        arguments = listOf(
            navArgument(CollectPhotoArgs.PHOTO_ID) {
                type = NavType.StringType
                nullable = false
            }
        )
    ) { navBackStackEntry ->
        val systemBarsColor = Color.Transparent
        val areIconsDark = !isSystemInDarkTheme()

        LaunchedEffect(key1 = true) {
            systemUiController.setSystemBarsColor(
                color = systemBarsColor,
                darkIcons = areIconsDark
            )
        }

        val id = navBackStackEntry.arguments?.getString(CollectPhotoArgs.PHOTO_ID).orEmpty()

        val viewModel: CollectPhotoViewModel = hiltViewModel()

        BackHandler(
            enabled = true,
            onBack = {
                navController.previousBackStackEntry
                    ?.savedStateHandle
                    ?.set(InterScreenCommunicationKeys.COLLECT_SCREEN_RESULT_KEY, viewModel.isPhotoCollected)

                navController.popBackStack()
            }
        )

        val context = LocalContext.current
        LaunchedEffect(true) {
            viewModel.errorFlow.collect { errorText ->
                context.toast(errorText.asString(context))
            }
        }

        CollectPhotoScreen(
            photoId = PhotoId(id),
            userCollections = viewModel.userCollections,
            isCollectionInList = viewModel::isCollectionInList,
            collectPhoto = viewModel::collectPhoto,
            dropPhoto = viewModel::dropPhotoFromCollection,
            createAndCollect = viewModel::createCollectionNewAndCollect,
            onNavigateBack = {
                navController.previousBackStackEntry
                    ?.savedStateHandle
                    ?.set(InterScreenCommunicationKeys.COLLECT_SCREEN_RESULT_KEY, viewModel.isPhotoCollected)

                navController.popBackStack()
            }
        )
    }
}

fun NavController.navigateToCollectPhoto(photoId: PhotoId) {
    this.navigate("${Screen.CollectPhoto.route}/${photoId.value}")
}

object CollectPhotoArgs {
    const val PHOTO_ID = "photo_id"
}
