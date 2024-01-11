package com.andrii_a.walleria.ui.search

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.andrii_a.walleria.domain.models.search.RecentSearchItem
import com.andrii_a.walleria.domain.repository.LocalPreferencesRepository
import com.andrii_a.walleria.domain.repository.RecentSearchesRepository
import com.andrii_a.walleria.domain.repository.SearchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
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
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchRepository: SearchRepository,
    private val recentSearchesRepository: RecentSearchesRepository,
    localPreferencesRepository: LocalPreferencesRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state: MutableStateFlow<SearchUiState> = MutableStateFlow(SearchUiState())
    val state = combine(
        recentSearchesRepository.getAllRecentSearches(),
        localPreferencesRepository.photosListLayoutType,
        localPreferencesRepository.collectionsListLayoutType,
        localPreferencesRepository.photosLoadQuality,
        _state
    ) { recentSearches, photosListLayoutType, collectionsListLayoutType, photosLoadQuality, state ->
        state.copy(
            recentSearches = recentSearches,
            photosLayoutType = photosListLayoutType,
            collectionsLayoutType = collectionsListLayoutType,
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
        savedStateHandle.get<String>(SearchArgs.QUERY)?.let { query ->
            if (query.isNotBlank()) {
                onEvent(SearchEvent.PerformSearch(query = query))
            }
        }
    }

    fun onEvent(event: SearchEvent) {
        when (event) {
            is SearchEvent.PerformSearch -> {
                performSearch(event.query)
            }

            is SearchEvent.ChangePhotoFilters -> {
                changePhotoFilters(event.photoFilters)
            }

            is SearchEvent.DeleteRecentSearchItem -> {
                deleteRecentSearch(event.item)
            }

            is SearchEvent.DeleteAllRecentSearches -> {
                deleteAllRecentSearches()
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

    private fun performSearch(query: String) {
        saveRecentQuery(query)

        _state.update {
            it.copy(
                query = query,
                photos = searchRepository.searchPhotos(
                    query = query,
                    order = it.photoFilters.order,
                    contentFilter = it.photoFilters.contentFilter,
                    color = it.photoFilters.color,
                    orientation = it.photoFilters.orientation
                ).cachedIn(viewModelScope),
                collections = searchRepository.searchCollections(query).cachedIn(viewModelScope),
                users = searchRepository.searchUsers(query).cachedIn(viewModelScope)
            )
        }
    }

    private fun saveRecentQuery(query: String) {
        viewModelScope.launch {
            withContext(NonCancellable) {
                val itemToModify = recentSearchesRepository.getRecentSearchByTitle(title = query)
                itemToModify?.let {
                    recentSearchesRepository.updateItem(
                        it.copy(timeMillis = System.currentTimeMillis())
                    )
                } ?: run {
                    val newRecentSearchItem = RecentSearchItem(
                        title = query,
                        timeMillis = System.currentTimeMillis()
                    )

                    recentSearchesRepository.insertItem(newRecentSearchItem)
                }
            }
        }
    }

    private fun deleteRecentSearch(item: RecentSearchItem) {
        viewModelScope.launch {
            withContext(NonCancellable) {
                recentSearchesRepository.deleteItem(item)
            }
        }
    }

    private fun deleteAllRecentSearches() {
        viewModelScope.launch {
            withContext(NonCancellable) {
                recentSearchesRepository.deleteAllItems()
            }
        }
    }

    private fun changePhotoFilters(filters: PhotoFilters) {
        _state.update {
            it.copy(
                photoFilters = filters,
                photos = searchRepository.searchPhotos(
                    query = it.query,
                    order = filters.order,
                    contentFilter = filters.contentFilter,
                    color = filters.color,
                    orientation = filters.orientation
                ).cachedIn(viewModelScope)
            )
        }
    }
}