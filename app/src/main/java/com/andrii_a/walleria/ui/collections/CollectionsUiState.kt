package com.andrii_a.walleria.ui.collections

import androidx.compose.runtime.Stable
import androidx.paging.PagingData
import com.andrii_a.walleria.domain.PhotoQuality
import com.andrii_a.walleria.domain.models.collection.Collection
import com.andrii_a.walleria.ui.util.emptyPagingData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

@Stable
data class CollectionsUiState(
    private val collectionsPagingData: PagingData<Collection> = emptyPagingData(),
    val photosLoadQuality: PhotoQuality = PhotoQuality.MEDIUM
) {
    private val _collections: MutableStateFlow<PagingData<Collection>> = MutableStateFlow(
        emptyPagingData()
    )
    val collections: StateFlow<PagingData<Collection>> = _collections.asStateFlow()

    init {
        _collections.update { collectionsPagingData }
    }
}
