package com.andrii_a.walleria.ui.user_details

import androidx.paging.PagingData
import com.andrii_a.walleria.domain.CollectionListLayoutType
import com.andrii_a.walleria.domain.PhotoQuality
import com.andrii_a.walleria.domain.PhotosListLayoutType
import com.andrii_a.walleria.domain.models.collection.Collection
import com.andrii_a.walleria.domain.models.photo.Photo
import com.andrii_a.walleria.domain.models.user.User
import com.andrii_a.walleria.ui.common.UiError
import com.andrii_a.walleria.ui.common.UserNickname
import com.andrii_a.walleria.ui.util.emptyPagingData

data class UserDetailsUiState(
    val loggedInUserNickname: UserNickname = "",
    val user: User? = null,
    val isLoading: Boolean = false,
    val error: UiError? = null,
    val isDetailsDialogOpened: Boolean = false,
    val photosPagingData: PagingData<Photo> = emptyPagingData(),
    val likedPhotosPagingData: PagingData<Photo> = emptyPagingData(),
    val collectionsPagingData: PagingData<Collection> = emptyPagingData(),
    val photosListLayoutType: PhotosListLayoutType = PhotosListLayoutType.DEFAULT,
    val collectionListLayoutType: CollectionListLayoutType = CollectionListLayoutType.DEFAULT,
    val photosLoadQuality: PhotoQuality = PhotoQuality.MEDIUM,
)
