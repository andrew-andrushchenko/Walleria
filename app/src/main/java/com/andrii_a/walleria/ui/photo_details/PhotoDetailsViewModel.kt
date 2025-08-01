package com.andrii_a.walleria.ui.photo_details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.andrii_a.walleria.domain.PhotoQuality
import com.andrii_a.walleria.domain.models.photo.Photo
import com.andrii_a.walleria.domain.network.Resource
import com.andrii_a.walleria.domain.repository.LocalPreferencesRepository
import com.andrii_a.walleria.domain.repository.PhotoRepository
import com.andrii_a.walleria.domain.repository.LocalAccountRepository
import com.andrii_a.walleria.domain.services.PhotoDownloader
import com.andrii_a.walleria.ui.common.PhotoId
import com.andrii_a.walleria.ui.common.UiErrorWithRetry
import com.andrii_a.walleria.ui.common.UiText
import com.andrii_a.walleria.ui.navigation.Screen
import kotlinx.coroutines.Job
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
import kotlin.math.max

class PhotoDetailsViewModel(
    private val photoRepository: PhotoRepository,
    localAccountRepository: LocalAccountRepository,
    localPreferencesRepository: LocalPreferencesRepository,
    private val photoDownloader: PhotoDownloader,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state: MutableStateFlow<PhotoDetailsUiState> = MutableStateFlow(
        PhotoDetailsUiState()
    )
    val state = combine(
        localAccountRepository.isUserLoggedIn,
        localPreferencesRepository.photosDownloadQuality,
        _state
    ) { isUserLoggedIn, photosDownloadQuality, state ->
        state.copy(
            isUserLoggedIn = isUserLoggedIn,
            photoDownloadQuality = photosDownloadQuality
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = _state.value
    )

    private val navigationChannel = Channel<PhotoDetailsNavigationEvent>()
    val navigationEventsChannelFlow = navigationChannel.receiveAsFlow()

    init {
        val photoId = savedStateHandle.toRoute<Screen.PhotoDetails>().photoId
        onEvent(PhotoDetailsEvent.RequestPhoto(photoId))
    }

    fun onEvent(event: PhotoDetailsEvent) {
        when (event) {
            is PhotoDetailsEvent.RequestPhoto -> {
                getPhoto(event.photoId)
            }

            is PhotoDetailsEvent.LikePhoto -> {
                likePhoto(event.photoId)
            }

            is PhotoDetailsEvent.DislikePhoto -> {
                dislikePhoto(event.photoId)
            }

            is PhotoDetailsEvent.MakeCollected -> {
                _state.update { it.copy(isCollected = true) }
            }

            is PhotoDetailsEvent.MakeDropped -> {
                _state.update { it.copy(isCollected = false) }
            }

            is PhotoDetailsEvent.DownloadPhoto -> {
                downloadPhoto(event.photo, event.quality)
            }

            is PhotoDetailsEvent.GoBack -> {
                viewModelScope.launch {
                    navigationChannel.send(PhotoDetailsNavigationEvent.NavigateBack)
                }
            }

            is PhotoDetailsEvent.SearchByTag -> {
                viewModelScope.launch {
                    navigationChannel.send(PhotoDetailsNavigationEvent.NavigateToSearch(event.query))
                }
            }

            is PhotoDetailsEvent.SelectCollection -> {
                viewModelScope.launch {
                    navigationChannel.send(
                        PhotoDetailsNavigationEvent.NavigateToCollectionDetails(
                            event.collectionId
                        )
                    )
                }
            }

            is PhotoDetailsEvent.SelectUser -> {
                viewModelScope.launch {
                    navigationChannel.send(PhotoDetailsNavigationEvent.NavigateToUserDetails(event.userNickname))
                }
            }

            is PhotoDetailsEvent.SelectCollectOption -> {
                viewModelScope.launch {
                    navigationChannel.send(
                        PhotoDetailsNavigationEvent.NavigateToCollectPhoto(
                            event.photoId
                        )
                    )
                }
            }

            is PhotoDetailsEvent.OpenInBrowser -> {
                viewModelScope.launch {
                    navigationChannel.send(
                        PhotoDetailsNavigationEvent.NavigateToChromeCustomTab(
                            event.url
                        )
                    )
                }
            }

            is PhotoDetailsEvent.ShowInfoDialog -> {
                _state.update {
                    it.copy(isInfoDialogOpened = true)
                }
            }

            is PhotoDetailsEvent.DismissInfoDialog -> {
                _state.update {
                    it.copy(isInfoDialogOpened = false)
                }
            }

            is PhotoDetailsEvent.RedirectToLogin -> {
                viewModelScope.launch {
                    navigationChannel.send(PhotoDetailsNavigationEvent.NavigateToLogin)
                }
            }

            is PhotoDetailsEvent.SharePhoto -> {
                viewModelScope.launch {
                    navigationChannel.send(
                        PhotoDetailsNavigationEvent.NavigateToShareDialog(
                            link = event.link,
                            description = event.description
                        )
                    )
                }
            }

            is PhotoDetailsEvent.ToggleControlsVisibility -> {
                _state.update {
                    it.copy(showControls = !it.showControls)
                }
            }

            is PhotoDetailsEvent.UpdateZoomToFillCoefficient -> {
                updateZoomToFillCoefficient(
                    imageWidth = event.imageWidth,
                    imageHeight = event.imageHeight,
                    containerWidth = event.containerWidth,
                    containerHeight = event.containerHeight
                )
            }
        }
    }

    private fun getPhoto(photoId: PhotoId) {
        photoRepository.getPhoto(photoId).onEach { result ->
            when (result) {
                is Resource.Loading -> {
                    _state.update {
                        it.copy(isLoading = true)
                    }
                }

                is Resource.Success -> {
                    val photo = result.value

                    val isPhotoCollected = photo.currentUserCollections?.map { collection ->
                        collection.id
                    }?.isNotEmpty() ?: false

                    _state.update {
                        it.copy(
                            isLoading = false,
                            photo = photo,
                            currentPhotoLikes = photo.likes,
                            error = null,
                            isLikedByLoggedInUser = photo.likedByUser,
                            isCollected = isPhotoCollected
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
                                    onEvent(PhotoDetailsEvent.RequestPhoto(photoId))
                                }
                            )
                        )
                    }
                }

                is Resource.Empty -> Unit
            }
        }.launchIn(viewModelScope)
    }

    private var likePhotoJob: Job? = null
    private var dislikePhotoJob: Job? = null

    private fun likePhoto(photoId: PhotoId) {
        likePhotoJob?.cancel()
        likePhotoJob = photoRepository.likePhoto(photoId).onEach { result ->
            when (result) {
                is Resource.Empty, Resource.Loading -> Unit
                is Resource.Error -> {
                    _state.update {
                        it.copy(
                            error = UiErrorWithRetry(reason = UiText.DynamicString(result.reason.orEmpty())),
                            isLikedByLoggedInUser = false
                        )
                    }
                }
                is Resource.Success<*> -> {
                    _state.update {
                        it.copy(
                            isLikedByLoggedInUser = true,
                            currentPhotoLikes = it.currentPhotoLikes?.plus(1)
                        )
                    }
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun dislikePhoto(photoId: PhotoId) {
        dislikePhotoJob?.cancel()
        dislikePhotoJob = photoRepository.dislikePhoto(photoId).onEach { result ->
            when (result) {
                is Resource.Empty, Resource.Loading -> Unit
                is Resource.Error -> {
                    _state.update {
                        it.copy(
                            error = UiErrorWithRetry(reason = UiText.DynamicString(result.reason.orEmpty())),
                            isLikedByLoggedInUser = true
                        )
                    }
                }
                is Resource.Success<*> -> {
                    _state.update {
                        it.copy(
                            isLikedByLoggedInUser = false,
                            currentPhotoLikes = it.photo?.likes
                        )
                    }
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun downloadPhoto(photo: Photo, quality: PhotoQuality) {
        photoDownloader.downloadPhoto(photo, quality)
    }

    private fun updateZoomToFillCoefficient(
        imageWidth: Float,
        imageHeight: Float,
        containerWidth: Float,
        containerHeight: Float
    ) {
        val widthRatio = imageWidth / imageHeight
        val height = containerWidth / widthRatio
        val zoomScaleH = containerHeight / height

        val heightRatio = imageHeight / imageWidth
        val width = containerHeight / heightRatio
        val zoomScaleW = containerWidth / width

        val coefficient =  max(zoomScaleW, zoomScaleH)

        _state.update {
            it.copy(zoomToFillCoefficient = coefficient)
        }
    }
}