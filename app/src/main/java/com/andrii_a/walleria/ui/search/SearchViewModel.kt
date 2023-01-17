package com.andrii_a.walleria.ui.search

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.andrii_a.walleria.core.SearchResultsContentFilter
import com.andrii_a.walleria.core.SearchResultsDisplayOrder
import com.andrii_a.walleria.core.SearchResultsPhotoColor
import com.andrii_a.walleria.core.SearchResultsPhotoOrientation
import com.andrii_a.walleria.domain.models.collection.Collection
import com.andrii_a.walleria.domain.models.photo.Photo
import com.andrii_a.walleria.domain.models.user.User
import com.andrii_a.walleria.domain.repository.SearchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

sealed interface SearchScreenEvent {
    data class OnQueryChanged(val query: String) : SearchScreenEvent

    data class OnPhotoFiltersChanged(val photoFilters: PhotoFilters) : SearchScreenEvent
}

data class PhotoFilters(
    val order: SearchResultsDisplayOrder,
    val contentFilter: SearchResultsContentFilter,
    val color: SearchResultsPhotoColor,
    val orientation: SearchResultsPhotoOrientation
)

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchRepository: SearchRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _query: MutableStateFlow<String> = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private val _photoFilters: MutableStateFlow<PhotoFilters> = MutableStateFlow(
        PhotoFilters(
            order = SearchResultsDisplayOrder.RELEVANT,
            contentFilter = SearchResultsContentFilter.LOW,
            color = SearchResultsPhotoColor.ANY,
            orientation = SearchResultsPhotoOrientation.ANY
        )
    )
    val photoFilters: StateFlow<PhotoFilters> = _photoFilters.asStateFlow()

    init {
        savedStateHandle.get<String>(SearchArgs.QUERY)?.let { query ->
            dispatchEvent(SearchScreenEvent.OnQueryChanged(query = query))
        }
    }

    fun dispatchEvent(event: SearchScreenEvent) {
        when (event) {
            is SearchScreenEvent.OnQueryChanged -> {
                _query.value = event.query
            }
            is SearchScreenEvent.OnPhotoFiltersChanged -> {
                _photoFilters.value = event.photoFilters
            }
        }
    }

    val photos: Flow<PagingData<Photo>> = combine(
        _query, _photoFilters
    ) { searchQuery, filters ->
        Pair(searchQuery, filters)
    }.flatMapLatest { (searchQuery, filters) ->
        searchRepository.searchPhotos(
            query = searchQuery,
            order = filters.order,
            contentFilter = filters.contentFilter,
            color = filters.color,
            orientation = filters.orientation
        ).cachedIn(viewModelScope)
    }

    val collections: Flow<PagingData<Collection>> = _query.flatMapLatest {
        searchRepository.searchCollections(it).cachedIn(viewModelScope)
    }

    val users: Flow<PagingData<User>> = _query.flatMapLatest {
        searchRepository.searchUsers(it).cachedIn(viewModelScope)
    }
}