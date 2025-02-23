package com.andrii_a.walleria.ui.collections

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.andrii_a.walleria.domain.repository.CollectionRepository
import com.andrii_a.walleria.domain.repository.LocalPreferencesRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CollectionsViewModel(
    private val collectionRepository: CollectionRepository,
    localPreferencesRepository: LocalPreferencesRepository
) : ViewModel() {

    private val _state: MutableStateFlow<CollectionsUiState> = MutableStateFlow(CollectionsUiState())
    val state = combine(
        localPreferencesRepository.photosLoadQuality,
        _state
    ) { photosLoadQuality, state ->
        state.copy(
            photosLoadQuality = photosLoadQuality
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = _state.value
    )

    private val navigationChannel = Channel<CollectionsNavigationEvent>()
    val navigationEventsChannelFlow = navigationChannel.receiveAsFlow()

    init {
        onEvent(CollectionsEvent.GetCollections)
    }

    fun onEvent(event: CollectionsEvent) {
        when (event) {
            is CollectionsEvent.GetCollections -> {
                viewModelScope.launch {
                    collectionRepository.getCollections().cachedIn(viewModelScope)
                        .collect { pagingData ->
                            _state.update {
                                it.copy(collectionsPagingData = pagingData)
                            }
                        }
                }
            }

            is CollectionsEvent.SelectCollection -> {
                viewModelScope.launch {
                    navigationChannel.send(
                        CollectionsNavigationEvent.NavigateToCollectionDetails(
                            event.collectionId
                        )
                    )
                }
            }

            is CollectionsEvent.SelectPhoto -> {
                viewModelScope.launch {
                    navigationChannel.send(
                        CollectionsNavigationEvent.NavigateToPhotoDetailsScreen(
                            event.photoId
                        )
                    )
                }
            }

            is CollectionsEvent.SelectSearch -> {
                viewModelScope.launch {
                    navigationChannel.send(CollectionsNavigationEvent.NavigateToSearchScreen)
                }
            }

            is CollectionsEvent.SelectUser -> {
                viewModelScope.launch {
                    navigationChannel.send(CollectionsNavigationEvent.NavigateToUserDetails(event.userNickname))
                }
            }
        }
    }

}