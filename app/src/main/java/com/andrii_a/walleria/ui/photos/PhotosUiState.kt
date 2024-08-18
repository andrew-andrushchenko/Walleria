package com.andrii_a.walleria.ui.photos

import androidx.paging.PagingData
import com.andrii_a.walleria.domain.PhotoListDisplayOrder
import com.andrii_a.walleria.domain.PhotoQuality
import com.andrii_a.walleria.domain.PhotosListLayoutType
import com.andrii_a.walleria.domain.models.photo.Photo

data class PhotosUiState(
    val photosPagingData: PagingData<Photo> = PagingData.empty(),
    val photosListDisplayOrder: PhotoListDisplayOrder = PhotoListDisplayOrder.LATEST,
    val photosListLayoutType: PhotosListLayoutType = PhotosListLayoutType.DEFAULT,
    val photosLoadQuality: PhotoQuality = PhotoQuality.MEDIUM
)