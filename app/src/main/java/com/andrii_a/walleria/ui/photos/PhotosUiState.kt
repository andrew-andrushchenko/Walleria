package com.andrii_a.walleria.ui.photos

import androidx.compose.runtime.Stable
import androidx.paging.PagingData
import com.andrii_a.walleria.domain.PhotoListDisplayOrder
import com.andrii_a.walleria.domain.PhotoQuality
import com.andrii_a.walleria.domain.models.photo.Photo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

@Stable
data class PhotosUiState(
    val photos: Flow<PagingData<Photo>> = emptyFlow(),
    val isOrderMenuExpanded: Boolean = false,
    val photosListDisplayOrder: PhotoListDisplayOrder = PhotoListDisplayOrder.LATEST,
    val photosLoadQuality: PhotoQuality = PhotoQuality.MEDIUM
)