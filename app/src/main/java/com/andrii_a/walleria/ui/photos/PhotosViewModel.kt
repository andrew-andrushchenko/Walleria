package com.andrii_a.walleria.ui.photos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.andrii_a.walleria.domain.PhotoListDisplayOrder
import com.andrii_a.walleria.domain.repository.LocalPreferencesRepository
import com.andrii_a.walleria.domain.repository.PhotoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PhotosViewModel @Inject constructor(
    private val photoRepository: PhotoRepository,
    localPreferencesRepository: LocalPreferencesRepository
) : ViewModel() {

    private val _state: MutableStateFlow<PhotosUiState> = MutableStateFlow(PhotosUiState())
    val state = combine(
        localPreferencesRepository.photosListLayoutType,
        localPreferencesRepository.photosLoadQuality,
        _state
    ) { photosListLayoutType, photosLoadQuality, state ->
        state.copy(
            photosListLayoutType = photosListLayoutType,
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

            is PhotosEvent.SelectPrivateUserProfile -> {
                viewModelScope.launch {
                    navigationChannel.send(PhotosNavigationEvent.NavigateToProfileScreen)
                }
            }

            is PhotosEvent.SelectSearch -> {
                viewModelScope.launch {
                    navigationChannel.send(PhotosNavigationEvent.NavigateToSearchScreen)
                }
            }
        }
    }
}