package com.andrii_a.walleria.ui.collect_photo

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.andrii_a.walleria.domain.network.BackendResult
import com.andrii_a.walleria.domain.repository.CollectionRepository
import com.andrii_a.walleria.domain.repository.PhotoRepository
import com.andrii_a.walleria.domain.repository.UserAccountPreferencesRepository
import com.andrii_a.walleria.ui.collect_photo.event.CollectPhotoEvent
import com.andrii_a.walleria.ui.collect_photo.event.CollectPhotoNavigationEvent
import com.andrii_a.walleria.ui.collect_photo.state.CollectActionState
import com.andrii_a.walleria.ui.collect_photo.state.CollectPhotoUiState
import com.andrii_a.walleria.ui.collect_photo.state.CollectionMetadata
import com.andrii_a.walleria.ui.common.CollectionId
import com.andrii_a.walleria.ui.common.PhotoId
import com.andrii_a.walleria.ui.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CollectPhotoViewModel @Inject constructor(
    private val photoRepository: PhotoRepository,
    private val collectionRepository: CollectionRepository,
    private val userAccountPreferencesRepository: UserAccountPreferencesRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state: MutableStateFlow<CollectPhotoUiState> = MutableStateFlow(CollectPhotoUiState())
    val state: StateFlow<CollectPhotoUiState> = _state.asStateFlow()

    private val navigationChannel = Channel<CollectPhotoNavigationEvent>()
    val navigationEventsChannelFlow = navigationChannel.receiveAsFlow()

    init {
        viewModelScope.launch {
            _state.update {
                val photoId = savedStateHandle.get<String>(CollectPhotoArgs.PHOTO_ID).orEmpty()
                val userCollectionsContainingPhoto = photoRepository.getUserCollectionIdsForPhoto(photoId)

                it.copy(
                    photoId = photoId,
                    userCollectionsContainingPhoto = userCollectionsContainingPhoto,
                    isLoading = true
                )
            }

            refreshCollectionsList()
        }
    }

    private suspend fun refreshCollectionsList() {
        val userPrivateProfileData = userAccountPreferencesRepository.userPrivateProfileData.firstOrNull() ?: return

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

        viewModelScope.launch {
            val deferredResult = async {
                collectionRepository.addPhotoToCollection(collectionId, photoId)
            }

            when (val result = deferredResult.await()) {
                is BackendResult.Empty -> Unit
                is BackendResult.Loading -> {
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

                is BackendResult.Error -> {
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

                is BackendResult.Success -> {
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
        }
    }

    private fun dropPhotoFromCollection(
        collectionId: CollectionId,
        photoId: PhotoId
    ) {
        val initialCollectionMetadata = CollectionMetadata(
            id = collectionId,
            state = CollectActionState.Collected
        )

        viewModelScope.launch {
            val deferredResult = async {
                collectionRepository.deletePhotoFromCollection(collectionId, photoId)
            }

            when (val result = deferredResult.await()) {
                is BackendResult.Empty -> Unit
                is BackendResult.Loading -> {
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

                is BackendResult.Error -> {
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

                is BackendResult.Success -> {
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
    }

    private fun createCollectionAndCollect(
        title: String,
        description: String? = null,
        isPrivate: Boolean = false,
        photoId: PhotoId
    ) {
        viewModelScope.launch {
            val creationResult = collectionRepository.createCollection(title, description, isPrivate)

            when (creationResult) {
                is BackendResult.Empty -> Unit
                is BackendResult.Loading -> {
                    _state.update {
                        it.copy(isCreateCollectionInProgress = true)
                    }
                }

                is BackendResult.Error -> {
                    _state.update {
                        it.copy(
                            error = CollectOperationError(
                                reason = UiText.DynamicString(creationResult.reason.orEmpty())
                            )
                        )
                    }
                }

                is BackendResult.Success -> {
                    val collectionId = creationResult.value.id
                    collectPhoto(collectionId, photoId)
                    refreshCollectionsList()
                }
            }
        }
    }
}
