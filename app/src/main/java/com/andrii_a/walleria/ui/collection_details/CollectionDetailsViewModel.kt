package com.andrii_a.walleria.ui.collection_details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import androidx.paging.cachedIn
import com.andrii_a.walleria.domain.network.Resource
import com.andrii_a.walleria.domain.repository.CollectionRepository
import com.andrii_a.walleria.domain.repository.LocalPreferencesRepository
import com.andrii_a.walleria.domain.repository.PhotoRepository
import com.andrii_a.walleria.domain.repository.LocalAccountRepository
import com.andrii_a.walleria.ui.common.CollectionId
import com.andrii_a.walleria.ui.common.UiErrorWithRetry
import com.andrii_a.walleria.ui.common.UiText
import com.andrii_a.walleria.ui.navigation.Screen
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CollectionDetailsViewModel(
    private val collectionRepository: CollectionRepository,
    private val photoRepository: PhotoRepository,
    localPreferencesRepository: LocalPreferencesRepository,
    localAccountRepository: LocalAccountRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state: MutableStateFlow<CollectionDetailsUiState> = MutableStateFlow(
        CollectionDetailsUiState()
    )
    val state = combine(
        localAccountRepository.userPrivateProfileData,
        localPreferencesRepository.photosLoadQuality,
        _state
    ) { userPrivateProfileData, photosLoadQuality, state ->
        state.copy(
            loggedInUserNickname = userPrivateProfileData.nickname,
            photosLoadQuality = photosLoadQuality
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = _state.value
    )

    private val navigationChannel = Channel<CollectionDetailsNavigationEvent>()
    val navigationEventsChannelFlow = navigationChannel.receiveAsFlow()

    init {
        val collectionId = savedStateHandle.toRoute<Screen.CollectionDetails>().collectionId
        onEvent(CollectionDetailsEvent.RequestCollection(collectionId))
    }

    fun onEvent(event: CollectionDetailsEvent) {
        when (event) {
            is CollectionDetailsEvent.RequestCollection -> {
                getCollection(event.collectionId)
            }

            is CollectionDetailsEvent.UpdateCollection -> {
                updateCollection(
                    event.collectionId,
                    event.title,
                    event.description,
                    event.isPrivate
                )
            }

            is CollectionDetailsEvent.DeleteCollection -> {
                deleteCollection(event.collectionId)
            }

            is CollectionDetailsEvent.GoBack -> {
                viewModelScope.launch {
                    navigationChannel.send(CollectionDetailsNavigationEvent.NavigateBack)
                }
            }

            is CollectionDetailsEvent.SelectPhoto -> {
                viewModelScope.launch {
                    navigationChannel.send(
                        CollectionDetailsNavigationEvent.NavigateToPhotoDetails(
                            event.photoId
                        )
                    )
                }
            }

            is CollectionDetailsEvent.SelectUser -> {
                viewModelScope.launch {
                    navigationChannel.send(
                        CollectionDetailsNavigationEvent.NavigateToUserDetails(
                            event.userNickname
                        )
                    )
                }
            }
        }
    }

    private fun getCollection(collectionId: CollectionId) {
        collectionRepository.getCollection(collectionId).onEach { result ->
            when (result) {
                is Resource.Empty -> Unit
                is Resource.Loading -> {
                    _state.update {
                        it.copy(
                            isLoading = true
                        )
                    }
                }

                is Resource.Error -> {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = UiErrorWithRetry(
                                reason = UiText.DynamicString(result.reason.orEmpty()),
                                onRetry = {
                                    onEvent(
                                        CollectionDetailsEvent.RequestCollection(
                                            collectionId
                                        )
                                    )
                                }
                            )
                        )
                    }
                }


                is Resource.Success -> {
                    val collection = result.value

                    viewModelScope.launch {
                        photoRepository.getCollectionPhotos(collection.id).cachedIn(viewModelScope)
                            .collect { pagingData ->
                                _state.update {
                                    it.copy(
                                        isLoading = false,
                                        error = null,
                                        collection = collection,
                                        collectionPhotosPagingData = pagingData
                                    )
                                }
                            }
                    }

                }
            }
        }.launchIn(viewModelScope)
    }

    private fun updateCollection(
        collectionId: CollectionId,
        title: String,
        description: String?,
        isPrivate: Boolean
    ) {
        viewModelScope.launch {
            collectionRepository.updateCollection(collectionId, title, description, isPrivate).collect {

            }
        }
    }

    private fun deleteCollection(collectionId: CollectionId) {
        viewModelScope.launch {
            collectionRepository.deleteCollection(collectionId).collect {

            }
        }
    }

}