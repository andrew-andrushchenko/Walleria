package com.andrii_a.walleria.ui.collect_photo.state

import androidx.compose.runtime.Stable
import androidx.paging.PagingData
import com.andrii_a.walleria.domain.models.collection.Collection
import com.andrii_a.walleria.ui.common.PhotoId
import com.andrii_a.walleria.ui.common.UiError
import com.andrii_a.walleria.ui.util.emptyPagingData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

@Stable
data class CollectPhotoUiState(
    val photoId: PhotoId = "",
    val userCollectionsContainingPhoto: List<String> = emptyList(),
    val error: UiError? = null,
    val isLoading: Boolean = false,
    val isCreateCollectionInProgress: Boolean = false,
    private val userCollectionsPagingData: PagingData<Collection> = emptyPagingData(),
    val modifiedCollectionMetadata: CollectionMetadata? = null,
    val isCreateDialogOpened: Boolean = false
) {
    val userCollections: StateFlow<PagingData<Collection>> field = MutableStateFlow(emptyPagingData())

    val isCollected = userCollectionsContainingPhoto.isNotEmpty()

    init {
        userCollections.update { userCollectionsPagingData }
    }
}