package com.andrii_a.walleria.ui.collect_photo

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import androidx.paging.cachedIn
import com.andrii_a.walleria.domain.network.Resource
import com.andrii_a.walleria.domain.repository.CollectionRepository
import com.andrii_a.walleria.domain.repository.PhotoRepository
import com.andrii_a.walleria.domain.repository.LocalAccountRepository
import com.andrii_a.walleria.ui.collect_photo.event.CollectPhotoEvent
import com.andrii_a.walleria.ui.collect_photo.event.CollectPhotoNavigationEvent
import com.andrii_a.walleria.ui.collect_photo.state.CollectActionState
import com.andrii_a.walleria.ui.collect_photo.state.CollectPhotoUiState
import com.andrii_a.walleria.ui.collect_photo.state.CollectionMetadata
import com.andrii_a.walleria.ui.common.CollectionId
import com.andrii_a.walleria.ui.common.PhotoId
import com.andrii_a.walleria.ui.common.UiText
import com.andrii_a.walleria.ui.navigation.Screen
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CollectPhotoViewModel(
    photoRepository: PhotoRepository,
    private val collectionRepository: CollectionRepository,
    private val localAccountRepository: LocalAccountRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state: MutableStateFlow<CollectPhotoUiState> = MutableStateFlow(CollectPhotoUiState())
    val state: StateFlow<CollectPhotoUiState> = _state.asStateFlow()

    private val navigationChannel = Channel<CollectPhotoNavigationEvent>()
    val navigationEventsChannelFlow = navigationChannel.receiveAsFlow()

    init {
        val photoId = savedStateHandle.toRoute<Screen.CollectPhoto>().photoId
        photoRepository.getUserCollectionIdsForPhoto(photoId)
            .onEach { result ->
                if (result is Resource.Success) {
                    _state.update { state ->
                        state.copy(
                            photoId = photoId,
                            userCollectionsContainingPhoto = result.value,
                            isLoading = true
                        )
                    }
                }
            }
            .onCompletion {
                refreshCollectionsList()
            }
            .launchIn(viewModelScope)
    }

    private suspend fun refreshCollectionsList() {
        val userPrivateProfileData =
            localAccountRepository.userPrivateProfileData.firstOrNull() ?: return

        collectionRepository.getUserCollections(userPrivateProfileData.nickname)
            .cachedIn(viewModelScope)
            .catch { e ->
                _state.update {
                    it.copy(
                        error = ListLoadingError(
                            reason = UiText.DynamicString(e.message.toString())
                        ),
                        isLoading = false
                    )
                }
            }
            .collect { pagingData ->
                _state.update {
                    it.copy(
                        userCollectionsPagingData = pagingData,
                        isLoading = false,
                        error = null
                    )
                }
            }
    }

    fun onEvent(event: CollectPhotoEvent) {
        when (event) {
            is CollectPhotoEvent.CollectPhoto -> {
                collectPhoto(
                    collectionId = event.collectionId,
                    photoId = event.photoId
                )
            }

            is CollectPhotoEvent.DropPhotoFromCollection -> {
                dropPhotoFromCollection(
                    collectionId = event.collectionId,
                    photoId = event.photoId
                )
            }

            is CollectPhotoEvent.CreateCollectionAndCollect -> {
                createCollectionAndCollect(
                    title = event.title,
                    description = event.description,
                    isPrivate = event.isPrivate,
                    photoId = event.photoId
                )
            }

            is CollectPhotoEvent.GoBack -> {
                viewModelScope.launch {
                    navigationChannel.send(CollectPhotoNavigationEvent.NavigateBack)
                }
            }

            is CollectPhotoEvent.OpenCreateAndCollectDialog -> {
                _state.update {
                    it.copy(isCreateDialogOpened = true)
                }
            }

            is CollectPhotoEvent.DismissCreateAndCollectDialog -> {
                _state.update {
                    it.copy(isCreateDialogOpened = false)
                }
            }

        }
    }

    private fun collectPhoto(
        collectionId: CollectionId,
        photoId: PhotoId
    ) {
        val initialCollectionMetadata = CollectionMetadata(
            id = collectionId,
            state = CollectActionState.NotCollected
        )

        collectionRepository.addPhotoToCollection(collectionId, photoId)
            .onEach { result ->
                when (result) {
                    is Resource.Empty -> Unit
                    is Resource.Loading -> {
                        _state.update {
                            it.copy(
                                error = null,
                                modifiedCollectionMetadata = CollectionMetadata(
                                    id = collectionId,
                                    state = CollectActionState.Loading
                                )
                            )
                        }
                    }

                    is Resource.Error -> {
                        _state.update {
                            it.copy(
                                error = CollectOperationError(
                                    reason = UiText.DynamicString(result.reason.orEmpty())
                                ),
                                modifiedCollectionMetadata = initialCollectionMetadata,
                                isCreateCollectionInProgress = false
                            )
                        }
                    }

                    is Resource.Success -> {
                        _state.update {
                            val newCollectionsList = it.userCollectionsContainingPhoto.toMutableList()
                            newCollectionsList += collectionId

                            it.copy(
                                error = null,
                                userCollectionsContainingPhoto = newCollectionsList,
                                isCreateCollectionInProgress = false
                            )
                        }
                    }
                }
            }.launchIn(viewModelScope)
    }

    private fun dropPhotoFromCollection(
        collectionId: CollectionId,
        photoId: PhotoId
    ) {
        val initialCollectionMetadata = CollectionMetadata(
            id = collectionId,
            state = CollectActionState.Collected
        )

        collectionRepository.deletePhotoFromCollection(collectionId, photoId)
            .onEach { result ->
                when (result) {
                    is Resource.Empty -> Unit
                    is Resource.Loading -> {
                        _state.update {
                            it.copy(
                                error = null,
                                modifiedCollectionMetadata = CollectionMetadata(
                                    id = collectionId,
                                    state = CollectActionState.Loading
                                )
                            )
                        }
                    }

                    is Resource.Error -> {
                        _state.update {
                            it.copy(
                                error = CollectOperationError(
                                    reason = UiText.DynamicString(result.reason.orEmpty())
                                ),
                                modifiedCollectionMetadata = initialCollectionMetadata,
                                isCreateCollectionInProgress = false
                            )
                        }
                    }

                    is Resource.Success -> {
                        _state.update {
                            val newCollectionsList = it.userCollectionsContainingPhoto.toMutableList()
                            newCollectionsList -= collectionId

                            it.copy(
                                error = null,
                                userCollectionsContainingPhoto = newCollectionsList,
                                isCreateCollectionInProgress = false
                            )
                        }
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    private fun createCollectionAndCollect(
        title: String,
        description: String? = null,
        isPrivate: Boolean = false,
        photoId: PhotoId
    ) {
        collectionRepository.createCollection(title, description, isPrivate)
            .onEach { result ->
                when (result) {
                    is Resource.Empty -> Unit
                    is Resource.Loading -> {
                        _state.update {
                            it.copy(isCreateCollectionInProgress = true)
                        }
                    }

                    is Resource.Error -> {
                        _state.update {
                            it.copy(
                                error = CollectOperationError(
                                    reason = UiText.DynamicString(result.reason.orEmpty())
                                )
                            )
                        }
                    }

                    is Resource.Success -> {
                        val collectionId = result.value.id
                        collectPhoto(collectionId, photoId)
                        refreshCollectionsList()
                    }
                }
            }
            .launchIn(viewModelScope)
    }
}
