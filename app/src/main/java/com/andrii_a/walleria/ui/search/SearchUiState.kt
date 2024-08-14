package com.andrii_a.walleria.ui.search

import androidx.paging.PagingData
import com.andrii_a.walleria.domain.CollectionListLayoutType
import com.andrii_a.walleria.domain.PhotoQuality
import com.andrii_a.walleria.domain.PhotosListLayoutType
import com.andrii_a.walleria.domain.models.collection.Collection
import com.andrii_a.walleria.domain.models.photo.Photo
import com.andrii_a.walleria.domain.models.search.RecentSearchItem
import com.andrii_a.walleria.domain.models.user.User
import com.andrii_a.walleria.ui.common.SearchQuery
import com.andrii_a.walleria.ui.util.emptyPagingDataFlow
import kotlinx.coroutines.flow.Flow

data class SearchUiState(
    val query: SearchQuery = "",
    val photoFilters: PhotoFilters = PhotoFilters(),
    val recentSearches: List<RecentSearchItem> = emptyList(),
    val photos: Flow<PagingData<Photo>> = emptyPagingDataFlow(),
    val collections: Flow<PagingData<Collection>> = emptyPagingDataFlow(),
    val users: Flow<PagingData<User>> = emptyPagingDataFlow(),
    val isFilterDialogOpened: Boolean = false,
    val photosLayoutType: PhotosListLayoutType = PhotosListLayoutType.DEFAULT,
    val collectionsLayoutType: CollectionListLayoutType = CollectionListLayoutType.DEFAULT,
    val photosLoadQuality: PhotoQuality = PhotoQuality.MEDIUM,
)
