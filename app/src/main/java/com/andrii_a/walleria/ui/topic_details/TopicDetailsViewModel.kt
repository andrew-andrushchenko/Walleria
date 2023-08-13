package com.andrii_a.walleria.ui.topic_details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.andrii_a.walleria.core.BackendResult
import com.andrii_a.walleria.domain.PhotoListDisplayOrder
import com.andrii_a.walleria.domain.TopicPhotosOrientation
import com.andrii_a.walleria.domain.models.photo.Photo
import com.andrii_a.walleria.domain.models.topic.Topic
import com.andrii_a.walleria.domain.repository.PhotoRepository
import com.andrii_a.walleria.domain.repository.TopicRepository
import com.andrii_a.walleria.ui.common.TopicId
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import javax.inject.Inject

sealed interface TopicDetailsEvent {
    data class RequestTopic(val topicId: TopicId) : TopicDetailsEvent

    data class ChangeFilters(val topicPhotosFilters: TopicPhotosFilters) : TopicDetailsEvent
}

sealed interface TopicLoadResult {
    data object Empty : TopicLoadResult
    data object Loading : TopicLoadResult
    data class Error(val topicId: String) : TopicLoadResult
    data class Success(
        val topic: Topic,
        val currentFilters: TopicPhotosFilters,
        val topicPhotos: Flow<PagingData<Photo>>,
    ) : TopicLoadResult
}

@HiltViewModel
class TopicDetailsViewModel @Inject constructor(
    private val topicRepository: TopicRepository,
    private val photoRepository: PhotoRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _loadResult: MutableStateFlow<TopicLoadResult> = MutableStateFlow(TopicLoadResult.Empty)
    val loadResult: StateFlow<TopicLoadResult> = _loadResult.asStateFlow()

    init {
        savedStateHandle.get<String>(TopicDetailsArgs.ID)?.let { id ->
            onEvent(TopicDetailsEvent.RequestTopic(TopicId(id)))
        }
    }

    fun onEvent(event: TopicDetailsEvent) {
        when (event) {
            is TopicDetailsEvent.RequestTopic -> {
                getTopic(event.topicId.value)
            }

            is TopicDetailsEvent.ChangeFilters -> {
                filterTopicPhotos(event.topicPhotosFilters)
            }
        }
    }

    private fun getTopic(id: String) {
        topicRepository.getTopic(id).onEach { result ->
            when (result) {
                is BackendResult.Empty -> Unit
                is BackendResult.Loading -> {
                    _loadResult.update { TopicLoadResult.Loading }
                }

                is BackendResult.Error -> {
                    _loadResult.update { TopicLoadResult.Error(topicId = id) }
                }

                is BackendResult.Success -> {
                    val topic = result.value

                    _loadResult.update {
                        TopicLoadResult.Success(
                            topic = topic,
                            currentFilters = TopicPhotosFilters(
                                order = PhotoListDisplayOrder.LATEST,
                                orientation = TopicPhotosOrientation.LANDSCAPE
                            ),
                            topicPhotos = photoRepository.getTopicPhotos(topic.id).cachedIn(viewModelScope)
                        )
                    }
                }
            }

        }.launchIn(viewModelScope)
    }

    private fun filterTopicPhotos(topicPhotosFilters: TopicPhotosFilters) {
        if (_loadResult.value is TopicLoadResult.Success) {
            val currentSuccessfulResult = _loadResult.value as TopicLoadResult.Success

            _loadResult.update {
                currentSuccessfulResult.copy(
                    currentFilters = topicPhotosFilters,
                    topicPhotos = photoRepository.getTopicPhotos(
                        idOrSlug = currentSuccessfulResult.topic.id,
                        orientation = topicPhotosFilters.orientation,
                        order = topicPhotosFilters.order
                    ).cachedIn(viewModelScope)
                )
            }
        }
    }

}