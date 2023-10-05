package com.andrii_a.walleria.ui.collect_photo

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.andrii_a.walleria.R
import com.andrii_a.walleria.core.BackendResult
import com.andrii_a.walleria.domain.models.collection.Collection
import com.andrii_a.walleria.domain.models.photo.Photo
import com.andrii_a.walleria.domain.repository.CollectionRepository
import com.andrii_a.walleria.domain.repository.UserAccountPreferencesRepository
import com.andrii_a.walleria.domain.repository.PhotoRepository
import com.andrii_a.walleria.ui.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class CollectState(val newCoverPhoto: Photo?) {
    data object Loading : CollectState(null)
    data class Collected(val coverPhoto: Photo?) : CollectState(coverPhoto)
    data class NotCollected(val coverPhoto: Photo?) : CollectState(coverPhoto)
}

sealed interface CollectionCreationResult {
    data object Loading : CollectionCreationResult
    data object Error : CollectionCreationResult
    data class Success(val coverPhoto: Photo?) : CollectionCreationResult
}

@HiltViewModel
class CollectPhotoViewModel @Inject constructor(
    private val photoRepository: PhotoRepository,
    private val collectionRepository: CollectionRepository,
    userAccountPreferencesRepository: UserAccountPreferencesRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val userNickname: StateFlow<String> =
        userAccountPreferencesRepository
            .myProfileData
            .map { it.nickname }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000L),
                initialValue = "",
            )

    private val _userCollectionsContainingPhoto: MutableStateFlow<MutableList<String>> =
        MutableStateFlow(mutableListOf())

    private val _errorFlow: MutableSharedFlow<UiText> = MutableSharedFlow()
    val errorFlow: SharedFlow<UiText> = _errorFlow.asSharedFlow()

    init {
        savedStateHandle.get<String>(CollectPhotoArgs.PHOTO_ID)?.let { photoId ->
            viewModelScope.launch {
                _userCollectionsContainingPhoto.update {
                    photoRepository.getUserCollectionIdsForPhoto(photoId).toMutableList()
                }
            }
        }
    }

    fun isCollectionInList(collectionId: String): Boolean =
        _userCollectionsContainingPhoto.value.contains(collectionId)

    val isPhotoCollected: Boolean
        get() = _userCollectionsContainingPhoto.value.isNotEmpty()

    val userCollections: Flow<PagingData<Collection>> = userNickname.flatMapLatest { nickname ->
        collectionRepository.getUserCollections(nickname).cachedIn(viewModelScope)
    }

    // TODO(Andrii): Maybe split this by two independent calls (create, collect)
    fun createCollectionNewAndCollect(
        title: String,
        description: String?,
        isPrivate: Boolean,
        photoId: String
    ): SharedFlow<CollectionCreationResult> = flow {
        emit(CollectionCreationResult.Loading)

        val creationResult = collectionRepository.createCollection(title, description, isPrivate)

        when (creationResult) {
            is BackendResult.Error -> {
                emit(CollectionCreationResult.Error)
                _errorFlow.emit(UiText.StringResource(id = R.string.unable_to_create_collection))
            }

            is BackendResult.Success -> {
                val newCollection = creationResult.value

                val additionResult =
                    collectionRepository.addPhotoToCollection(newCollection.id, photoId)
                if (additionResult is BackendResult.Success) {
                    val newIdList = _userCollectionsContainingPhoto.value
                    newIdList.add(newCollection.id)
                    _userCollectionsContainingPhoto.update { newIdList }

                    emit(CollectionCreationResult.Success(additionResult.value.photo))
                }
            }

            else -> Unit
        }
    }.shareIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L)
    )

    fun collectPhoto(
        collectionId: String,
        photoId: String
    ): SharedFlow<CollectState> = flow {
        emit(CollectState.Loading)

        val result = collectionRepository.addPhotoToCollection(collectionId, photoId)

        when (result) {
            is BackendResult.Empty -> Unit
            is BackendResult.Error -> {
                emit(CollectState.NotCollected(null))
                _errorFlow.emit(UiText.StringResource(id = R.string.unable_to_collect_photo))
            }

            is BackendResult.Loading -> {
                emit(CollectState.Loading)
            }

            is BackendResult.Success -> {
                val newIdList = _userCollectionsContainingPhoto.value
                newIdList += collectionId
                _userCollectionsContainingPhoto.update { newIdList }

                emit(CollectState.Collected(result.value.collection?.coverPhoto)) // TODO: Maybe replace by result.value.photo?
            }
        }
    }.shareIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L)
    )

    fun dropPhotoFromCollection(
        collectionId: String,
        photoId: String
    ): SharedFlow<CollectState> = flow {
        emit(CollectState.Loading)

        val result = collectionRepository.deletePhotoFromCollection(collectionId, photoId)

        when (result) {
            is BackendResult.Empty -> Unit
            is BackendResult.Error -> {
                emit(CollectState.Collected(null))
                _errorFlow.emit(UiText.StringResource(id = R.string.unable_to_drop_photo))
            }

            is BackendResult.Loading -> {
                emit(CollectState.Loading)
            }

            is BackendResult.Success -> {
                val newList = _userCollectionsContainingPhoto.value
                newList -= collectionId
                _userCollectionsContainingPhoto.update { newList }

                emit(CollectState.NotCollected(result.value.collection?.coverPhoto))
            }
        }
    }.shareIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L)
    )

}