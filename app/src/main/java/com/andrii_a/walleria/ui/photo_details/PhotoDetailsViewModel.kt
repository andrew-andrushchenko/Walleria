package com.andrii_a.walleria.ui.photo_details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andrii_a.walleria.core.BackendResult
import com.andrii_a.walleria.domain.PhotoQuality
import com.andrii_a.walleria.domain.models.photo.Photo
import com.andrii_a.walleria.domain.repository.UserAccountPreferencesRepository
import com.andrii_a.walleria.domain.repository.PhotoRepository
import com.andrii_a.walleria.domain.services.PhotoDownloader
import com.andrii_a.walleria.ui.common.PhotoId
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
    data class RequestPhoto(val photoId: PhotoId) : PhotoDetailsEvent
    data class LikePhoto(val photoId: PhotoId) : PhotoDetailsEvent
    data class DislikePhoto(val photoId: PhotoId) : PhotoDetailsEvent
    data object CollectPhoto : PhotoDetailsEvent
    data object DropPhoto : PhotoDetailsEvent
    data class DownloadPhoto(
        val photo: Photo,
        val quality: PhotoQuality = PhotoQuality.HIGH
    ) : PhotoDetailsEvent
}

sealed interface PhotoLoadResult {
    data object Empty : PhotoLoadResult
    data object Loading : PhotoLoadResult
    data class Error(val photoId: PhotoId) : PhotoLoadResult
    data class Success(val photo: Photo) : PhotoLoadResult
}

@HiltViewModel
class PhotoDetailsViewModel @Inject constructor(
    private val photoRepository: PhotoRepository,
    userAccountPreferencesRepository: UserAccountPreferencesRepository,
    private val photoDownloader: PhotoDownloader,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    val isUserLoggedIn: StateFlow<Boolean> = userAccountPreferencesRepository.isUserAuthorized
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = false
        )

    private val _loadResult: MutableStateFlow<PhotoLoadResult> = MutableStateFlow(PhotoLoadResult.Empty)
    val loadResult: StateFlow<PhotoLoadResult> = _loadResult.asStateFlow()

    private val _isLiked: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isLiked: StateFlow<Boolean> = _isLiked.asStateFlow()

    private val _isCollected: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isCollected: StateFlow<Boolean> = _isCollected.asStateFlow()

    init {
        savedStateHandle.get<String>(PhotoDetailsArgs.ID)?.let { photoId ->
            onEvent(PhotoDetailsEvent.RequestPhoto(PhotoId(photoId)))
        }
    }

    fun onEvent(event: PhotoDetailsEvent) {
        when (event) {
            is PhotoDetailsEvent.RequestPhoto -> getPhoto(event.photoId)
            is PhotoDetailsEvent.LikePhoto -> likePhoto(event.photoId)
            is PhotoDetailsEvent.DislikePhoto -> dislikePhoto(event.photoId)
            is PhotoDetailsEvent.CollectPhoto -> _isCollected.update { true }
            is PhotoDetailsEvent.DropPhoto -> _isCollected.update { false }
            is PhotoDetailsEvent.DownloadPhoto -> downloadPhoto(event.photo, event.quality)
        }
    }

    private fun getPhoto(photoId: PhotoId) {
        photoRepository.getPhoto(photoId.value).onEach { result ->
            when (result) {
                is BackendResult.Loading -> {
                    _loadResult.update { PhotoLoadResult.Loading }
                }
                is BackendResult.Success -> {
                    val photo = result.value
                    _loadResult.update { PhotoLoadResult.Success(photo) }
                    _isLiked.update { photo.likedByUser }
                    _isCollected.update { photo.currentUserCollections?.map { it.id }?.isNotEmpty() ?: false }
                }
                is BackendResult.Error -> {
                    _loadResult.update { PhotoLoadResult.Error(photoId) }
                }
                is BackendResult.Empty -> Unit
            }
        }.launchIn(viewModelScope)
    }

    private var likePhotoJob: Job? = null
    private var dislikePhotoJob: Job? = null

    private fun likePhoto(photoId: PhotoId) {
        likePhotoJob?.cancel()
        likePhotoJob = viewModelScope.launch {
            photoRepository.likePhoto(photoId.value)
            _isLiked.update { true }
        }
    }

    private fun dislikePhoto(photoId: PhotoId) {
        dislikePhotoJob?.cancel()
        dislikePhotoJob = viewModelScope.launch {
            photoRepository.dislikePhoto(photoId.value)
            _isLiked.update { false }
        }
    }

    private fun downloadPhoto(photo: Photo, quality: PhotoQuality) {
        photoDownloader.downloadPhoto(photo, quality)
    }
}