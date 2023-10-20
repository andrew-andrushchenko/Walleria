package com.andrii_a.walleria.ui.search

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.andrii_a.walleria.domain.CollectionListLayoutType
import com.andrii_a.walleria.domain.PhotoQuality
import com.andrii_a.walleria.domain.PhotosListLayoutType
import com.andrii_a.walleria.domain.SearchResultsContentFilter
import com.andrii_a.walleria.domain.SearchResultsDisplayOrder
import com.andrii_a.walleria.domain.SearchResultsPhotoColor
import com.andrii_a.walleria.domain.SearchResultsPhotoOrientation
import com.andrii_a.walleria.domain.models.collection.Collection
import com.andrii_a.walleria.domain.models.photo.Photo
import com.andrii_a.walleria.domain.models.search.RecentSearchItem
import com.andrii_a.walleria.domain.models.user.User
import com.andrii_a.walleria.domain.repository.LocalPreferencesRepository
import com.andrii_a.walleria.domain.repository.RecentSearchesRepository
import com.andrii_a.walleria.domain.repository.SearchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

sealed interface SearchScreenEvent {
    data class ChangeQuery(val query: String) : SearchScreenEvent

    data class ChangePhotoFilters(val photoFilters: PhotoFilters) : SearchScreenEvent

    data class SaveRecentSearch(val query: String) : SearchScreenEvent

    data class DeleteRecentSearch(val item: RecentSearchItem) : SearchScreenEvent

    data object DeleteAllRecentSearches : SearchScreenEvent
}

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchRepository: SearchRepository,
    private val recentSearchesRepository: RecentSearchesRepository,
    localPreferencesRepository: LocalPreferencesRepository,
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

    val recentSearches: StateFlow<List<RecentSearchItem>> =
        recentSearchesRepository.getAllRecentSearches()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000L),
                initialValue = emptyList()
            )

    val photosLayoutType: StateFlow<PhotosListLayoutType> =
        localPreferencesRepository.photosListLayoutType
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000L),
                initialValue = runBlocking { localPreferencesRepository.photosListLayoutType.first() }
            )

    val collectionsLayoutType: StateFlow<CollectionListLayoutType> =
        localPreferencesRepository.collectionsListLayoutType
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000L),
                initialValue = runBlocking { localPreferencesRepository.collectionsListLayoutType.first() }
            )

    val photosLoadQuality: StateFlow<PhotoQuality> = localPreferencesRepository.photosLoadQuality
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = runBlocking { localPreferencesRepository.photosLoadQuality.first() }
        )

    init {
        savedStateHandle.get<String>(SearchArgs.QUERY)?.let { query ->
            onEvent(SearchScreenEvent.ChangeQuery(query = query))
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
        )
    }.cachedIn(viewModelScope)

    val collections: Flow<PagingData<Collection>> = _query.flatMapLatest {
        searchRepository.searchCollections(it)
    }.cachedIn(viewModelScope)

    val users: Flow<PagingData<User>> = _query.flatMapLatest {
        searchRepository.searchUsers(it)
    }.cachedIn(viewModelScope)

    fun onEvent(event: SearchScreenEvent) {
        when (event) {
            is SearchScreenEvent.ChangeQuery -> {
                _query.update { event.query }
            }

            is SearchScreenEvent.ChangePhotoFilters -> {
                _photoFilters.update { event.photoFilters }
            }

            is SearchScreenEvent.SaveRecentSearch -> {
                saveRecentQuery(event.query)
            }

            is SearchScreenEvent.DeleteRecentSearch -> {
                deleteRecentSearch(event.item)
            }

            is SearchScreenEvent.DeleteAllRecentSearches -> {
                deleteAllRecentSearches()
            }
        }
    }

    private fun saveRecentQuery(query: String) {
        val searchQueries = recentSearches.value.map { it.title }

        if (searchQueries.contains(query)) {
            val itemToUpdate = recentSearches.value.findLast { it.title == query }
            itemToUpdate?.let {
                viewModelScope.launch {
                    recentSearchesRepository.updateItem(
                        it.copy(timeMillis = System.currentTimeMillis())
                    )
                }
            }

            return
        }

        val newRecentSearchItem = RecentSearchItem(
            title = query,
            timeMillis = System.currentTimeMillis()
        )

        viewModelScope.launch {
            recentSearchesRepository.insertItem(newRecentSearchItem)
        }
    }

    private fun deleteRecentSearch(item: RecentSearchItem) {
        viewModelScope.launch {
            recentSearchesRepository.deleteItem(item)
        }
    }

    private fun deleteAllRecentSearches() {
        viewModelScope.launch {
            recentSearchesRepository.deleteAllItems()
        }
    }
}