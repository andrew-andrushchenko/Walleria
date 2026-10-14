package com.andrii_a.walleria.ui.photos

import androidx.compose.runtime.Stable
import androidx.paging.PagingData
import com.andrii_a.walleria.domain.PhotoListDisplayOrder
import com.andrii_a.walleria.domain.PhotoQuality
import com.andrii_a.walleria.domain.models.photo.Photo
import com.andrii_a.walleria.ui.util.emptyPagingData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

@Stable
data class PhotosUiState(
    private val photosPagingData: PagingData<Photo> = emptyPagingData(),
    val isOrderMenuExpanded: Boolean = false,
    val photosListDisplayOrder: PhotoListDisplayOrder = PhotoListDisplayOrder.LATEST,
    val photosLoadQuality: PhotoQuality = PhotoQuality.MEDIUM
) {
    val photos: StateFlow<PagingData<Photo>> field = MutableStateFlow(emptyPagingData())

    init {
        photos.update { photosPagingData }
    }
}