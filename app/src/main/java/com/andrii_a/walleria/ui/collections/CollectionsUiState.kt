package com.andrii_a.walleria.ui.collections

import androidx.paging.PagingData
import com.andrii_a.walleria.domain.CollectionListLayoutType
import com.andrii_a.walleria.domain.PhotoQuality
import com.andrii_a.walleria.domain.models.collection.Collection
import com.andrii_a.walleria.ui.util.emptyPagingDataFlow
import kotlinx.coroutines.flow.Flow

data class CollectionsUiState(
    val collections: Flow<PagingData<Collection>> = emptyPagingDataFlow(),
    val collectionsLayoutType: CollectionListLayoutType = CollectionListLayoutType.DEFAULT,
    val photosLoadQuality: PhotoQuality = PhotoQuality.MEDIUM
)
