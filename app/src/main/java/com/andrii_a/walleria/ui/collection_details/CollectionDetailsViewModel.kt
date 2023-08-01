package com.andrii_a.walleria.ui.collection_details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.andrii_a.walleria.core.BackendResult
import com.andrii_a.walleria.domain.models.collection.Collection
import com.andrii_a.walleria.domain.models.photo.Photo
import com.andrii_a.walleria.domain.repository.CollectionRepository
import com.andrii_a.walleria.domain.repository.LocalUserAccountPreferencesRepository
import com.andrii_a.walleria.domain.repository.PhotoRepository
import com.andrii_a.walleria.ui.common.CollectionId
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface CollectionDetailsEvent {
    data class CollectionRequested(val collectionId: CollectionId) : CollectionDetailsEvent

    data class UpdateCollection(
        val collectionId: CollectionId,
        val title: String,
        val description: String?,
        val isPrivate: Boolean
    ) : CollectionDetailsEvent

    data class DeleteCollection(val collectionId: CollectionId) : CollectionDetailsEvent
}

sealed interface CollectionLoadResult {
    data object Empty : CollectionLoadResult
    data object Loading : CollectionLoadResult
    data object Error : CollectionLoadResult
    data class Success(
        val collection: Collection,
        val collectionPhotos: Flow<PagingData<Photo>>
    ) : CollectionLoadResult
}

@HiltViewModel
class CollectionDetailsViewModel @Inject constructor(
    private val collectionRepository: CollectionRepository,
    private val photoRepository: PhotoRepository,
    localUserAccountPreferencesRepository: LocalUserAccountPreferencesRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val loggedInUsername: StateFlow<String> =
        localUserAccountPreferencesRepository.myProfileData
            .map {
                it.nickname
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000L),
                initialValue = ""
            )

    private val _loadResult: MutableStateFlow<CollectionLoadResult> = MutableStateFlow(CollectionLoadResult.Empty)
    val loadResult: StateFlow<CollectionLoadResult> = _loadResult.asStateFlow()

    init {
        savedStateHandle.get<String>(CollectionDetailsArgs.ID)?.let { id ->
            getCollection(id)
        }
    }

    fun onEvent(event: CollectionDetailsEvent) {
        when (event) {
            is CollectionDetailsEvent.CollectionRequested -> {
                getCollection(event.collectionId.value)
            }

            is CollectionDetailsEvent.UpdateCollection -> {
                updateCollection(
                    event.collectionId.value,
                    event.title,
                    event.description,
                    event.isPrivate
                )
            }

            is CollectionDetailsEvent.DeleteCollection -> {
                deleteCollection(event.collectionId.value)
            }
        }
    }

    private fun getCollection(id: String) {
        collectionRepository.getCollection(id).onEach { result ->
            when (result) {
                is BackendResult.Empty -> Unit
                is BackendResult.Error -> {
                    _loadResult.update { CollectionLoadResult.Error }
                }

                is BackendResult.Loading -> {
                    _loadResult.update { CollectionLoadResult.Loading }
                }

                is BackendResult.Success -> {
                    _loadResult.update {
                        CollectionLoadResult.Success(
                            collection = result.value,
                            collectionPhotos = photoRepository.getCollectionPhotos(result.value.id)
                                .cachedIn(viewModelScope)
                        )
                    }
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun updateCollection(
        id: String,
        title: String,
        description: String?,
        isPrivate: Boolean
    ) {
        viewModelScope.launch {
            collectionRepository.updateCollection(id, title, description, isPrivate)
        }
    }

    private fun deleteCollection(id: String) {
        viewModelScope.launch {
            collectionRepository.deleteCollection(id)
        }
    }

}