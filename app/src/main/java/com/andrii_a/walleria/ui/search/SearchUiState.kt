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
import kotlinx.coroutines.flow.asStateFlow
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
    private val _photos: MutableStateFlow<PagingData<Photo>> = MutableStateFlow(emptyPagingData())
    val photos: StateFlow<PagingData<Photo>> = _photos.asStateFlow()

    private val _collections: MutableStateFlow<PagingData<Collection>> = MutableStateFlow(
        emptyPagingData()
    )
    val collections: StateFlow<PagingData<Collection>> = _collections.asStateFlow()

    private val _users: MutableStateFlow<PagingData<User>> = MutableStateFlow(emptyPagingData())
    val users: StateFlow<PagingData<User>> = _users.asStateFlow()

    init {
        _photos.update { photosPagingData }
        _collections.update { collectionsPagingData }
        _users.update { usersPagingData }
    }
}
