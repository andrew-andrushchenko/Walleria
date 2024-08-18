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
import com.andrii_a.walleria.ui.util.emptyPagingData

data class SearchUiState(
    val query: SearchQuery = "",
    val photoFilters: PhotoFilters = PhotoFilters(),
    val recentSearches: List<RecentSearchItem> = emptyList(),
    val photosPagingData: PagingData<Photo> = emptyPagingData(),
    val collectionsPagingData: PagingData<Collection> = emptyPagingData(),
    val usersPagingData: PagingData<User> = emptyPagingData(),
    val isFilterDialogOpened: Boolean = false,
    val photosLayoutType: PhotosListLayoutType = PhotosListLayoutType.DEFAULT,
    val collectionsLayoutType: CollectionListLayoutType = CollectionListLayoutType.DEFAULT,
    val photosLoadQuality: PhotoQuality = PhotoQuality.MEDIUM,
)
