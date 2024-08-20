package com.andrii_a.walleria.ui.collect_photo.state

import androidx.compose.runtime.Stable
import androidx.paging.PagingData
import com.andrii_a.walleria.domain.models.collection.Collection
import com.andrii_a.walleria.ui.common.PhotoId
import com.andrii_a.walleria.ui.common.UiError
import com.andrii_a.walleria.ui.util.emptyPagingData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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
    private val _userCollections: MutableStateFlow<PagingData<Collection>> = MutableStateFlow(
        emptyPagingData()
    )
    val userCollections: StateFlow<PagingData<Collection>> = _userCollections.asStateFlow()

    val isCollected = userCollectionsContainingPhoto.isNotEmpty()

    init {
        _userCollections.update { userCollectionsPagingData }
    }
}