package com.andrii_a.walleria.ui.collections

import androidx.compose.runtime.Stable
import androidx.paging.PagingData
import com.andrii_a.walleria.domain.PhotoQuality
import com.andrii_a.walleria.domain.models.collection.Collection
import com.andrii_a.walleria.ui.util.emptyPagingData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

@Stable
data class CollectionsUiState(
    private val collectionsPagingData: PagingData<Collection> = emptyPagingData(),
    val photosLoadQuality: PhotoQuality = PhotoQuality.MEDIUM
) {
    val collections: StateFlow<PagingData<Collection>> field = MutableStateFlow(emptyPagingData())

    init {
        collections.update { collectionsPagingData }
    }
}
