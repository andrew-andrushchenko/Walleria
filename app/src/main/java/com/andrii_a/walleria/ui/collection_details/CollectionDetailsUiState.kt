package com.andrii_a.walleria.ui.collection_details

import androidx.paging.PagingData
import com.andrii_a.walleria.domain.PhotoQuality
import com.andrii_a.walleria.domain.PhotosListLayoutType
import com.andrii_a.walleria.domain.models.collection.Collection
import com.andrii_a.walleria.domain.models.photo.Photo
import com.andrii_a.walleria.ui.common.UiError
import com.andrii_a.walleria.ui.common.UserNickname

data class CollectionDetailsUiState(
    val loggedInUserNickname: UserNickname = "",
    val isLoading: Boolean = false,
    val error: UiError? = null,
    val collection: Collection? = null,
    val collectionPhotosPagingData: PagingData<Photo> = PagingData.empty(),
    val photosListLayoutType: PhotosListLayoutType = PhotosListLayoutType.DEFAULT,
    val photosLoadQuality: PhotoQuality = PhotoQuality.MEDIUM
)
