package com.andrii_a.walleria.ui.photo_details

import com.andrii_a.walleria.domain.PhotoQuality
import com.andrii_a.walleria.domain.models.photo.Photo
import com.andrii_a.walleria.ui.util.UiError

data class PhotoDetailsUiState(
    val isUserLoggedIn: Boolean = false,
    val isLoading: Boolean = false,
    val error: UiError? = null,
    val photo: Photo? = null,
    val isLiked: Boolean = false,
    val isCollected: Boolean = false,
    val isInfoDialogOpened: Boolean = false,
    val photoDownloadQuality: PhotoQuality = PhotoQuality.HIGH
)