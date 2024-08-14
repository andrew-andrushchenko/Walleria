package com.andrii_a.walleria.ui.topic_details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.andrii_a.walleria.domain.network.BackendResult
import com.andrii_a.walleria.domain.repository.LocalPreferencesRepository
import com.andrii_a.walleria.domain.repository.PhotoRepository
import com.andrii_a.walleria.domain.repository.TopicRepository
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
class TopicDetailsViewModel @Inject constructor(
    private val topicRepository: TopicRepository,
    private val photoRepository: PhotoRepository,
    localPreferencesRepository: LocalPreferencesRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state: MutableStateFlow<TopicDetailsUiState> = MutableStateFlow(TopicDetailsUiState())
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

    private val navigationChannel = Channel<TopicDetailsNavigationEvent>()
    val navigationEventsChannelFlow = navigationChannel.receiveAsFlow()

    init {
        savedStateHandle.get<String>(TopicDetailsArgs.ID)?.let { topicId ->
            onEvent(TopicDetailsEvent.RequestTopic(topicId))
        }
    }

    fun onEvent(event: TopicDetailsEvent) {
        when (event) {
            is TopicDetailsEvent.RequestTopic -> {
                getTopic(event.topicId)
            }

            is TopicDetailsEvent.ChangeFilters -> {
                filterTopicPhotos(event.topicPhotosFilters)
            }

            is TopicDetailsEvent.GoBack -> {
                viewModelScope.launch {
                    navigationChannel.send(TopicDetailsNavigationEvent.NavigateBack)
                }
            }

            is TopicDetailsEvent.OpenFilterDialog -> {
                _state.update {
                    it.copy(isFilterDialogOpened = true)
                }
            }

            is TopicDetailsEvent.DismissFilterDialog -> {
                _state.update {
                    it.copy(isFilterDialogOpened = false)
                }
            }

            is TopicDetailsEvent.OpenInBrowser -> {
                viewModelScope.launch {
                    navigationChannel.send(TopicDetailsNavigationEvent.NavigateToChromeCustomTab(event.url))
                }
            }

            is TopicDetailsEvent.SelectPhoto -> {
                viewModelScope.launch {
                    navigationChannel.send(TopicDetailsNavigationEvent.NavigateToPhotoDetails(event.photoId))
                }
            }

            is TopicDetailsEvent.SelectUser -> {
                viewModelScope.launch {
                    navigationChannel.send(TopicDetailsNavigationEvent.NavigateToUserDetails(event.userNickname))
                }
            }
        }
    }

    private fun getTopic(id: String) {
        topicRepository.getTopic(id).onEach { result ->
            when (result) {
                is BackendResult.Empty -> Unit
                is BackendResult.Loading -> {
                    _state.update {
                        it.copy(isLoading = true)
                    }
                }

                is BackendResult.Error -> {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = UiError(
                                reason = UiText.DynamicString(result.reason.orEmpty()),
                                //onRetry = {}
                            )
                        )
                    }
                }

                is BackendResult.Success -> {
                    val topic = result.value
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = null,
                            topic = topic,
                            topicPhotos = photoRepository.getTopicPhotos(
                                idOrSlug = topic.id,
                                orientation = it.topicPhotosFilters.orientation,
                                order = it.topicPhotosFilters.order
                            ).cachedIn(viewModelScope)
                        )
                    }
                }
            }

        }.launchIn(viewModelScope)
    }

    private fun filterTopicPhotos(topicPhotosFilters: TopicPhotosFilters) {
        _state.update {
            it.copy(
                topicPhotosFilters = topicPhotosFilters,
                topicPhotos = photoRepository.getTopicPhotos(
                    idOrSlug = it.topic!!.id,
                    orientation = topicPhotosFilters.orientation,
                    order = topicPhotosFilters.order
                )
            )
        }
    }

}