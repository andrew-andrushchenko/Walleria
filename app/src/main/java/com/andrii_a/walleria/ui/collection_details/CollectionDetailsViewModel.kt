package com.andrii_a.walleria.ui.collection_details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.andrii_a.walleria.domain.network.BackendResult
import com.andrii_a.walleria.domain.repository.CollectionRepository
import com.andrii_a.walleria.domain.repository.LocalPreferencesRepository
import com.andrii_a.walleria.domain.repository.PhotoRepository
import com.andrii_a.walleria.domain.repository.UserAccountPreferencesRepository
import com.andrii_a.walleria.ui.common.CollectionId
import com.andrii_a.walleria.ui.util.UiError
import com.andrii_a.walleria.ui.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
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
import javax.inject.Inject

@HiltViewModel
class CollectionDetailsViewModel @Inject constructor(
    private val collectionRepository: CollectionRepository,
    private val photoRepository: PhotoRepository,
    localPreferencesRepository: LocalPreferencesRepository,
    userAccountPreferencesRepository: UserAccountPreferencesRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state: MutableStateFlow<CollectionDetailsUiState> = MutableStateFlow(
        CollectionDetailsUiState()
    )
    val state = combine(
        userAccountPreferencesRepository.userPrivateProfileData,
        localPreferencesRepository.photosListLayoutType,
        localPreferencesRepository.photosLoadQuality,
        _state
    ) { userPrivateProfileData, photosLayoutType, photosLoadQuality, state ->
        state.copy(
            loggedInUserNickname = userPrivateProfileData.nickname,
            photosListLayoutType = photosLayoutType,
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
        savedStateHandle.get<String>(CollectionDetailsArgs.ID)?.let { id ->
            getCollection(CollectionId(id))
        }
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

    private fun getCollection(id: CollectionId) {
        collectionRepository.getCollection(id.value).onEach { result ->
            when (result) {
                is BackendResult.Empty -> Unit
                is BackendResult.Loading -> {
                    _state.update {
                        it.copy(
                            isLoading = true
                        )
                    }
                }

                is BackendResult.Error -> {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = UiError(
                                reason = UiText.DynamicString(result.reason.orEmpty()),
                                onRetry = { onEvent(CollectionDetailsEvent.RequestCollection(id)) }
                            )
                        )
                    }
                }


                is BackendResult.Success -> {
                    val collection = result.value

                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = null,
                            collection = collection,
                            collectionPhotos = photoRepository.getCollectionPhotos(collection.id)
                                .cachedIn(viewModelScope)
                        )
                    }
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun updateCollection(
        id: CollectionId,
        title: String,
        description: String?,
        isPrivate: Boolean
    ) {
        viewModelScope.launch {
            collectionRepository.updateCollection(id.value, title, description, isPrivate)
        }
    }

    private fun deleteCollection(id: CollectionId) {
        viewModelScope.launch {
            collectionRepository.deleteCollection(id.value)
        }
    }

}