package com.andrii_a.walleria.ui.search

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import androidx.paging.cachedIn
import com.andrii_a.walleria.domain.models.search.SearchHistoryItem
import com.andrii_a.walleria.domain.repository.LocalPreferencesRepository
import com.andrii_a.walleria.domain.repository.SearchHistoryRepository
import com.andrii_a.walleria.domain.repository.SearchRepository
import com.andrii_a.walleria.ui.navigation.Screen
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchViewModel(
    private val searchRepository: SearchRepository,
    private val searchHistoryRepository: SearchHistoryRepository,
    localPreferencesRepository: LocalPreferencesRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state: MutableStateFlow<SearchUiState> = MutableStateFlow(SearchUiState())
    val state = combine(
        searchHistoryRepository.getSearchHistory(),
        localPreferencesRepository.photosLoadQuality,
        _state
    ) { searchHistory, photosLoadQuality, state ->
        state.copy(
            searchHistory = searchHistory,
            photosLoadQuality = photosLoadQuality
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = _state.value
    )

    private val navigationChannel = Channel<SearchNavigationEvent>()
    val navigationEventsChannelFlow = navigationChannel.receiveAsFlow()

    init {
        val searchQuery = savedStateHandle.toRoute<Screen.Search>().searchQuery
        if (searchQuery.isNotBlank()) {
            onEvent(SearchEvent.ChangeQuery(searchQuery))
            onEvent(SearchEvent.PerformSearch)
        }
    }

    fun onEvent(event: SearchEvent) {
        when (event) {
            is SearchEvent.ChangeQuery -> {
                _state.update {
                    it.copy(query = event.query)
                }
            }

            is SearchEvent.PerformSearch -> {
                performSearch()
            }

            is SearchEvent.ChangePhotoFilters -> {
                changePhotoFilters(event.photoFilters)
            }

            is SearchEvent.DeleteSearchHistoryItem -> {
                deleteSearchHistoryItem(event.item)
            }

            is SearchEvent.DeleteSearchHistory -> {
                deleteSearchHistory()
            }

            is SearchEvent.OpenFilterDialog -> {
                _state.update {
                    it.copy(isFilterDialogOpened = true)
                }
            }

            is SearchEvent.DismissFilterDialog -> {
                _state.update {
                    it.copy(isFilterDialogOpened = false)
                }
            }

            is SearchEvent.SelectPhoto -> {
                viewModelScope.launch {
                    navigationChannel.send(SearchNavigationEvent.NavigateToPhotoDetails(event.photoId))
                }
            }

            is SearchEvent.SelectCollection -> {
                viewModelScope.launch {
                    navigationChannel.send(SearchNavigationEvent.NavigateToCollectionDetails(event.collectionId))
                }
            }

            is SearchEvent.SelectUser -> {
                viewModelScope.launch {
                    navigationChannel.send(SearchNavigationEvent.NavigateToUserDetails(event.userNickname))
                }
            }

            is SearchEvent.GoBack -> {
                viewModelScope.launch {
                    navigationChannel.send(SearchNavigationEvent.NavigateBack)
                }
            }
        }
    }

    private fun performSearch() {
        val currentState = _state.value
        val query = currentState.query
        val filters = currentState.photoFilters

        saveRecentQuery(query)

        _state.update {
            it.copy(
                photos = searchRepository.searchPhotos(
                    query = query,
                    order = filters.order,
                    contentFilter = filters.contentFilter,
                    color = filters.color,
                    orientation = filters.orientation
                ).cachedIn(viewModelScope),
                collections = searchRepository.searchCollections(query).cachedIn(viewModelScope),
                users = searchRepository.searchUsers(query).cachedIn(viewModelScope)
            )
        }
    }

    private fun saveRecentQuery(query: String) {
        viewModelScope.launch {
            withContext(NonCancellable) {
                val itemToModify =
                    searchHistoryRepository.getSearchHistoryItemByTitle(title = query)
                itemToModify?.let {
                    searchHistoryRepository.updateItem(
                        it.copy(timeMillis = System.currentTimeMillis())
                    )
                } ?: run {
                    val newSearchHistoryItem = SearchHistoryItem(
                        title = query,
                        timeMillis = System.currentTimeMillis()
                    )

                    searchHistoryRepository.insertItem(newSearchHistoryItem)
                }
            }
        }
    }

    private fun deleteSearchHistoryItem(item: SearchHistoryItem) {
        viewModelScope.launch {
            withContext(NonCancellable) {
                searchHistoryRepository.deleteItem(item)
            }
        }
    }

    private fun deleteSearchHistory() {
        viewModelScope.launch {
            withContext(NonCancellable) {
                searchHistoryRepository.deleteAllItems()
            }
        }
    }

    private fun changePhotoFilters(filters: PhotoFilters) {
        _state.update { currentState ->
            currentState.copy(
                photoFilters = filters,
                photos = searchRepository.searchPhotos(
                    query = currentState.query,
                    order = filters.order,
                    contentFilter = filters.contentFilter,
                    color = filters.color,
                    orientation = filters.orientation
                ).cachedIn(viewModelScope)
            )
        }
    }
}