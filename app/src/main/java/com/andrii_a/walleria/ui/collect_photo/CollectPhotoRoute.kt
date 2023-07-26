package com.andrii_a.walleria.ui.collect_photo

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.andrii_a.walleria.ui.common.PhotoId
import com.andrii_a.walleria.ui.common.PhotoUrl
import com.andrii_a.walleria.ui.navigation.Screen
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.bottomSheet
import com.google.accompanist.systemuicontroller.SystemUiController
import kotlinx.coroutines.flow.collectLatest

private const val TAG = "CollectPhotoRoute"

@OptIn(ExperimentalMaterialNavigationApi::class, ExperimentalMaterialApi::class)
fun NavGraphBuilder.collectPhotoRoute(
    navController: NavController,
    systemUiController: SystemUiController,
    bottomSheetState: ModalBottomSheetState
) {
    bottomSheet(
        route = "${Screen.CollectPhoto.route}/{${CollectPhotoArgs.PHOTO_ID}}",
        arguments = listOf(
            navArgument(CollectPhotoArgs.PHOTO_ID) {
                type = NavType.StringType
                nullable = false
            }
        )
    ) { navBackStackEntry ->
        val id = navBackStackEntry.arguments?.getString(CollectPhotoArgs.PHOTO_ID).orEmpty()

        val viewModel: CollectPhotoViewModel = hiltViewModel()

        BackHandler(
            enabled = true,
            onBack = {
                navController.previousBackStackEntry
                    ?.savedStateHandle
                    ?.set("collect_result_key", viewModel.isPhotoCollected)

                navController.popBackStack()
            }
        )

        LaunchedEffect(Unit) {
            snapshotFlow { bottomSheetState.targetValue }
                .collectLatest { targetValue ->
                    if (bottomSheetState.currentValue == ModalBottomSheetValue.Expanded
                        && targetValue == ModalBottomSheetValue.Hidden
                    ) {
                        navController.previousBackStackEntry
                            ?.savedStateHandle
                            ?.set("collect_result_key", viewModel.isPhotoCollected)
                        Log.d(
                            TAG,
                            "collectPhotoRoute: snapshotFlow: backStackEntry updated. TargetValue = ${targetValue.name}"
                        )
                    }
                }
        }

        CollectPhotoScreen(
            photoId = PhotoId(id),
            userCollections = viewModel.userCollections,
            isCollectionInList = viewModel::isCollectionInList,
            collectPhoto = viewModel::collectPhoto,
            dropPhoto = viewModel::dropPhotoFromCollection,
            createAndCollect = viewModel::createCollectionNewAndCollect,
            errorFlow = viewModel.errorFlow
        )
    }
}

fun NavController.navigateToCollectPhoto(
    photoId: PhotoId,
    newCoverPhotoUrl: PhotoUrl // TODO: remove in the next commit. Reason: don't need anymore
) {
    this.navigate("${Screen.CollectPhoto.route}/${photoId.value}")
}

object CollectPhotoArgs {
    const val PHOTO_ID = "photo_id"
}
