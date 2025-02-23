package com.andrii_a.walleria.ui.photos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.andrii_a.walleria.domain.PhotoListDisplayOrder
import com.andrii_a.walleria.domain.repository.LocalPreferencesRepository
import com.andrii_a.walleria.domain.repository.PhotoRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PhotosViewModel(
    private val photoRepository: PhotoRepository,
    localPreferencesRepository: LocalPreferencesRepository
) : ViewModel() {

    private val _state: MutableStateFlow<PhotosUiState> = MutableStateFlow(PhotosUiState())
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

    private val navigationChannel = Channel<PhotosNavigationEvent>()
    val navigationEventsChannelFlow = navigationChannel.receiveAsFlow()

    init {
        onEvent(PhotosEvent.ChangeListOrder(orderOptionOrdinalNum = PhotoListDisplayOrder.LATEST.ordinal))
    }

    fun onEvent(event: PhotosEvent) {
        when (event) {
            is PhotosEvent.ChangeListOrder -> {
                val displayOrder = PhotoListDisplayOrder.entries[event.orderOptionOrdinalNum]
                viewModelScope.launch {
                    photoRepository.getPhotos(displayOrder)
                        .cachedIn(viewModelScope)
                        .collectLatest { pagingData ->
                            _state.update {
                                it.copy(
                                    photosListDisplayOrder = displayOrder,
                                    photosPagingData = pagingData,
                                )
                            }
                        }
                }
            }

            is PhotosEvent.SelectPhoto -> {
                viewModelScope.launch {
                    navigationChannel.send(
                        PhotosNavigationEvent.NavigateToPhotoDetailsScreen(event.photoId)
                    )
                }
            }

            is PhotosEvent.SelectUser -> {
                viewModelScope.launch {
                    navigationChannel.send(PhotosNavigationEvent.NavigateToUserDetails(event.userNickname))
                }
            }

            is PhotosEvent.SelectSearch -> {
                viewModelScope.launch {
                    navigationChannel.send(PhotosNavigationEvent.NavigateToSearchScreen)
                }
            }

            is PhotosEvent.ToggleListOrderMenu -> {
                _state.update {
                    it.copy(
                        isOrderMenuExpanded = event.isExpanded
                    )
                }
            }
        }
    }
}