package com.andrii_a.walleria.ui.photo_details

import androidx.compose.runtime.Stable
import com.andrii_a.walleria.domain.PhotoQuality
import com.andrii_a.walleria.domain.models.photo.Photo
import com.andrii_a.walleria.ui.common.UiError

@Stable
data class PhotoDetailsUiState(
    val isUserLoggedIn: Boolean = false,
    val isLoading: Boolean = false,
    val error: UiError? = null,
    val photo: Photo? = null,
    val currentPhotoLikes: Long? = 0,
    val isLikedByLoggedInUser: Boolean = false,
    val isCollected: Boolean = false,
    val isInfoDialogOpened: Boolean = false,
    val photoDownloadQuality: PhotoQuality = PhotoQuality.HIGH,
    val showControls: Boolean = true,
    val zoomToFillCoefficient: Float = 1f,
)