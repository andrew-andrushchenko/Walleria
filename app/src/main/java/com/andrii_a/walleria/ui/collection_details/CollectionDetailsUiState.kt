package com.andrii_a.walleria.ui.collection_details

import androidx.compose.runtime.Stable
import androidx.paging.PagingData
import com.andrii_a.walleria.domain.PhotoQuality
import com.andrii_a.walleria.domain.models.collection.Collection
import com.andrii_a.walleria.domain.models.photo.Photo
import com.andrii_a.walleria.ui.common.UiError
import com.andrii_a.walleria.ui.common.UserNickname
import com.andrii_a.walleria.ui.util.emptyPagingData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

@Stable
data class CollectionDetailsUiState(
    val loggedInUserNickname: UserNickname = "",
    val isLoading: Boolean = false,
    val error: UiError? = null,
    val collection: Collection? = null,
    private val collectionPhotosPagingData: PagingData<Photo> = emptyPagingData(),
    val photosLoadQuality: PhotoQuality = PhotoQuality.MEDIUM
) {
    private val _collectionPhotos: MutableStateFlow<PagingData<Photo>> = MutableStateFlow(
        emptyPagingData()
    )
    val collectionPhotos: StateFlow<PagingData<Photo>> = _collectionPhotos.asStateFlow()

    init {
        _collectionPhotos.update { collectionPhotosPagingData }
    }
}
