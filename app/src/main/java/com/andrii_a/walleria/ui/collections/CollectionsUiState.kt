package com.andrii_a.walleria.ui.collections

import androidx.paging.PagingData
import com.andrii_a.walleria.domain.CollectionListLayoutType
import com.andrii_a.walleria.domain.PhotoQuality
import com.andrii_a.walleria.domain.models.collection.Collection

data class CollectionsUiState(
    //val collections: Flow<PagingData<Collection>> = emptyPagingDataFlow(),
    val collectionsPagingData: PagingData<Collection> = PagingData.empty(),
    val collectionsLayoutType: CollectionListLayoutType = CollectionListLayoutType.DEFAULT,
    val photosLoadQuality: PhotoQuality = PhotoQuality.MEDIUM
)
