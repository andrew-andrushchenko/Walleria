package com.andrii_a.walleria.ui.topic_details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import androidx.paging.cachedIn
import com.andrii_a.walleria.domain.network.Resource
import com.andrii_a.walleria.domain.repository.LocalPreferencesRepository
import com.andrii_a.walleria.domain.repository.PhotoRepository
import com.andrii_a.walleria.domain.repository.TopicRepository
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

class TopicDetailsViewModel(
    private val topicRepository: TopicRepository,
    private val photoRepository: PhotoRepository,
    localPreferencesRepository: LocalPreferencesRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state: MutableStateFlow<TopicDetailsUiState> = MutableStateFlow(TopicDetailsUiState())
    val state = combine(
        localPreferencesRepository.photosLoadQuality,
        _state
    ) { photosLoadQuality, state ->
        state.copy(
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
        val topicId = savedStateHandle.toRoute<Screen.TopicDetails>().topicId
        onEvent(TopicDetailsEvent.RequestTopic(topicId))
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
                is Resource.Empty -> Unit
                is Resource.Loading -> {
                    _state.update {
                        it.copy(isLoading = true)
                    }
                }

                is Resource.Error -> {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = UiErrorWithRetry(
                                reason = UiText.DynamicString(result.reason.orEmpty()),
                                onRetry = {
                                    onEvent(TopicDetailsEvent.RequestTopic(id))
                                }
                            )
                        )
                    }
                }

                is Resource.Success -> {
                    val topic = result.value
                    viewModelScope.launch {
                        photoRepository.getTopicPhotos(
                            idOrSlug = topic.id,
                            orientation = _state.value.topicPhotosFilters.orientation,
                            order = _state.value.topicPhotosFilters.order
                        ).cachedIn(viewModelScope).collect { pagingData ->
                            _state.update {
                                it.copy(
                                    isLoading = false,
                                    error = null,
                                    topic = topic,
                                    topicPhotosPagingData = pagingData,
                                )
                            }
                        }
                    }
                }
            }

        }.launchIn(viewModelScope)
    }

    private fun filterTopicPhotos(topicPhotosFilters: TopicPhotosFilters) {
        viewModelScope.launch {
            photoRepository.getTopicPhotos(
                idOrSlug = _state.value.topic!!.id,
                orientation = topicPhotosFilters.orientation,
                order = topicPhotosFilters.order
            ).cachedIn(viewModelScope).collect { pagingData ->
                _state.update {
                    it.copy(
                        topicPhotosFilters = topicPhotosFilters,
                        topicPhotosPagingData = pagingData
                    )
                }
            }
        }
    }

}