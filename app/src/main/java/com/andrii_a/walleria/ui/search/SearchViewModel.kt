package com.andrii_a.walleria.ui.search

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import androidx.paging.cachedIn
import com.andrii_a.walleria.domain.models.search.RecentSearchItem
import com.andrii_a.walleria.domain.repository.LocalPreferencesRepository
import com.andrii_a.walleria.domain.repository.RecentSearchesRepository
import com.andrii_a.walleria.domain.repository.SearchRepository
import com.andrii_a.walleria.ui.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
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
        localPreferencesRepository.photosLoadQuality,
        _state
    ) { recentSearches, photosLoadQuality, state ->
        state.copy(
            recentSearches = recentSearches,
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
            onEvent(SearchEvent.PerformSearch(query = searchQuery))
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

            is SearchEvent.ToggleSearchBox -> {
                _state.update {
                    it.copy(isSearchBoxExpanded = event.isExpanded)
                }
            }
        }
    }

    private fun performSearch(query: String) {
        saveRecentQuery(query)

        val searchPhotoResultFlow = searchRepository.searchPhotos(
            query = query,
            order = _state.value.photoFilters.order,
            contentFilter = _state.value.photoFilters.contentFilter,
            color = _state.value.photoFilters.color,
            orientation = _state.value.photoFilters.orientation
        ).cachedIn(viewModelScope)

        val searchCollectionResultFlow = searchRepository.searchCollections(query).cachedIn(viewModelScope)
        val searchUserResultFlow = searchRepository.searchUsers(query).cachedIn(viewModelScope)

        combine(
            searchPhotoResultFlow,
            searchCollectionResultFlow,
            searchUserResultFlow
        ) { photosPagingData, collectionsPagingData, usersPagingData ->
            _state.update {
                it.copy(
                    query = query,
                    photosPagingData = photosPagingData,
                    collectionsPagingData = collectionsPagingData,
                    usersPagingData = usersPagingData
                )
            }
        }.launchIn(viewModelScope)
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
        viewModelScope.launch {
            searchRepository.searchPhotos(
                query = _state.value.query,
                order = filters.order,
                contentFilter = filters.contentFilter,
                color = filters.color,
                orientation = filters.orientation
            ).cachedIn(viewModelScope).collect { photoPagingData ->
                _state.update {
                    it.copy(
                        photoFilters = filters,
                        photosPagingData = photoPagingData,
                    )
                }
            }
        }
    }
}