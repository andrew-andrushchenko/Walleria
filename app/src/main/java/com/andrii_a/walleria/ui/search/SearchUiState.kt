package com.andrii_a.walleria.ui.search

import androidx.compose.runtime.Stable
import androidx.paging.PagingData
import com.andrii_a.walleria.domain.PhotoQuality
import com.andrii_a.walleria.domain.models.collection.Collection
import com.andrii_a.walleria.domain.models.photo.Photo
import com.andrii_a.walleria.domain.models.search.SearchHistoryItem
import com.andrii_a.walleria.domain.models.user.User
import com.andrii_a.walleria.ui.common.SearchQuery
import com.andrii_a.walleria.ui.util.emptyPagingData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

@Stable
data class SearchUiState(
    val query: SearchQuery = "",
    val photoFilters: PhotoFilters = PhotoFilters(),
    val searchHistory: List<SearchHistoryItem> = emptyList(),
    private val photosPagingData: PagingData<Photo> = emptyPagingData(),
    private val collectionsPagingData: PagingData<Collection> = emptyPagingData(),
    private val usersPagingData: PagingData<User> = emptyPagingData(),
    val isFilterDialogOpened: Boolean = false,
    val photosLoadQuality: PhotoQuality = PhotoQuality.MEDIUM,
) {
    val photos: StateFlow<PagingData<Photo>> field = MutableStateFlow(emptyPagingData())

    val collections: StateFlow<PagingData<Collection>> field = MutableStateFlow(emptyPagingData())

    val users: StateFlow<PagingData<User>> field = MutableStateFlow(emptyPagingData())

    init {
        photos.update { photosPagingData }
        collections.update { collectionsPagingData }
        users.update { usersPagingData }
    }
}
