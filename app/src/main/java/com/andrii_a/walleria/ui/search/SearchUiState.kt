package com.andrii_a.walleria.ui.search

import androidx.compose.runtime.Stable
import androidx.paging.PagingData
import com.andrii_a.walleria.domain.PhotoQuality
import com.andrii_a.walleria.domain.models.collection.Collection
import com.andrii_a.walleria.domain.models.photo.Photo
import com.andrii_a.walleria.domain.models.search.SearchHistoryItem
import com.andrii_a.walleria.domain.models.user.User
import com.andrii_a.walleria.ui.common.SearchQuery
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

@Stable
data class SearchUiState(
    val query: SearchQuery = "",
    val photoFilters: PhotoFilters = PhotoFilters(),
    val searchHistory: List<SearchHistoryItem> = emptyList(),
    val photos: Flow<PagingData<Photo>> = emptyFlow(),
    val collections: Flow<PagingData<Collection>> = emptyFlow(),
    val users: Flow<PagingData<User>> = emptyFlow(),
    val isFilterDialogOpened: Boolean = false,
    val photosLoadQuality: PhotoQuality = PhotoQuality.MEDIUM,
)