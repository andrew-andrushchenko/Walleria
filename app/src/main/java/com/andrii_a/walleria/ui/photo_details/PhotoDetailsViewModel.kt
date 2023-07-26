package com.andrii_a.walleria.ui.photo_details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andrii_a.walleria.core.BackendResult
import com.andrii_a.walleria.domain.models.photo.Photo
import com.andrii_a.walleria.domain.repository.LocalUserAccountPreferencesRepository
import com.andrii_a.walleria.domain.repository.PhotoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface PhotoDetailsEvent {
    data class PhotoRequested(val photoId: String) : PhotoDetailsEvent
    data class PhotoLiked(val photoId: String) : PhotoDetailsEvent
    data class PhotoDisliked(val photoId: String) : PhotoDetailsEvent
    data object PhotoBookmarked : PhotoDetailsEvent
    data object PhotoDropped : PhotoDetailsEvent
}

sealed interface PhotoLoadResult {
    data object Empty : PhotoLoadResult
    data object Loading : PhotoLoadResult
    data object Error : PhotoLoadResult
    data class Success(val photo: Photo) : PhotoLoadResult
}

@HiltViewModel
class PhotoDetailsViewModel @Inject constructor(
    private val photoRepository: PhotoRepository,
    localUserAccountPreferencesRepository: LocalUserAccountPreferencesRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    val isUserLoggedIn: StateFlow<Boolean> = localUserAccountPreferencesRepository.isUserAuthorized
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = false
        )

    private val _loadResult: MutableStateFlow<PhotoLoadResult> = MutableStateFlow(PhotoLoadResult.Empty)
    val loadResult: StateFlow<PhotoLoadResult> = _loadResult.asStateFlow()

    private val _isLiked: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isLiked: StateFlow<Boolean> = _isLiked.asStateFlow()

    private val _isBookmarked: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isBookmarked: StateFlow<Boolean> = _isBookmarked.asStateFlow()

    init {
        savedStateHandle.get<String>(PhotoDetailsArgs.ID)?.let { photoId ->
            dispatchEvent(PhotoDetailsEvent.PhotoRequested(photoId))
        }
    }

    fun dispatchEvent(event: PhotoDetailsEvent) {
        when (event) {
            is PhotoDetailsEvent.PhotoRequested -> getPhoto(event.photoId)
            is PhotoDetailsEvent.PhotoLiked -> likePhoto(event.photoId)
            is PhotoDetailsEvent.PhotoDisliked -> dislikePhoto(event.photoId)
            is PhotoDetailsEvent.PhotoBookmarked -> _isBookmarked.update { true }
            is PhotoDetailsEvent.PhotoDropped -> _isBookmarked.update { false }
        }
    }

    private fun getPhoto(photoId: String) {
        photoRepository.getPhoto(photoId).onEach { result ->
            when (result) {
                is BackendResult.Loading -> {
                    _loadResult.update { PhotoLoadResult.Loading }
                }
                is BackendResult.Success -> {
                    val photo = result.value
                    _loadResult.update { PhotoLoadResult.Success(photo) }
                    _isLiked.update { photo.likedByUser }
                    _isBookmarked.update { photo.currentUserCollections?.map { it.id }?.isNotEmpty() ?: false }
                }
                is BackendResult.Error -> {
                    _loadResult.update { PhotoLoadResult.Error }
                }
                is BackendResult.Empty -> Unit
            }
        }.launchIn(viewModelScope)
    }

    private var likePhotoJob: Job? = null
    private var dislikePhotoJob: Job? = null

    private fun likePhoto(photoId: String) {
        likePhotoJob?.cancel()
        likePhotoJob = viewModelScope.launch {
            photoRepository.likePhoto(photoId)
            _isLiked.update { true }
        }
    }

    private fun dislikePhoto(photoId: String) {
        dislikePhotoJob?.cancel()
        dislikePhotoJob = viewModelScope.launch {
            photoRepository.dislikePhoto(photoId)
            _isLiked.update { false }
        }
    }
}