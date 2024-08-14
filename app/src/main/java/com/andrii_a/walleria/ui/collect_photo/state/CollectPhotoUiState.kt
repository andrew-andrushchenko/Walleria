package com.andrii_a.walleria.ui.collect_photo.state

import androidx.paging.PagingData
import com.andrii_a.walleria.domain.models.collection.Collection
import com.andrii_a.walleria.ui.common.PhotoId
import com.andrii_a.walleria.ui.common.UiError

data class CollectPhotoUiState(
    val photoId: PhotoId = "",
    val userCollectionsContainingPhoto: List<String> = emptyList(),
    val error: UiError? = null,
    val isLoading: Boolean = false,
    val isCreateCollectionInProgress: Boolean = false,
    val userCollectionsPagingData: PagingData<Collection> = PagingData.empty(),
    val modifiedCollectionMetadata: CollectionMetadata? = null,
    val isCreateDialogOpened: Boolean = false
) {
    val isCollected = userCollectionsContainingPhoto.isNotEmpty()
}