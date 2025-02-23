package com.andrii_a.walleria.ui.topics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.andrii_a.walleria.domain.TopicsDisplayOrder
import com.andrii_a.walleria.domain.repository.TopicRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TopicsViewModel(private val topicRepository: TopicRepository) : ViewModel() {

    private val _state: MutableStateFlow<TopicsUiState> = MutableStateFlow(TopicsUiState())
    val state: StateFlow<TopicsUiState> = _state.asStateFlow()

    private val navigationChannel = Channel<TopicsNavigationEvent>()
    val navigationEventsChannelFlow = navigationChannel.receiveAsFlow()

    init {
        onEvent(TopicsEvent.ChangeListOrder(orderOptionOrdinalNum = TopicsDisplayOrder.LATEST.ordinal))
    }

    fun onEvent(event: TopicsEvent) {
        when (event) {
            is TopicsEvent.ChangeListOrder -> {
                val displayOrder = TopicsDisplayOrder.entries[event.orderOptionOrdinalNum]
                viewModelScope.launch {
                    topicRepository.getTopics(displayOrder).cachedIn(viewModelScope)
                        .collect { pagingData ->
                            _state.update {
                                it.copy(
                                    topicsDisplayOrder = displayOrder,
                                    topicsPagingData = pagingData
                                )
                            }
                        }
                }
            }

            is TopicsEvent.SelectTopic -> {
                viewModelScope.launch {
                    navigationChannel.send(TopicsNavigationEvent.NavigateToTopicDetails(event.topicId))
                }
            }

            is TopicsEvent.SelectSearch -> {
                viewModelScope.launch {
                    navigationChannel.send(TopicsNavigationEvent.NavigateToSearchScreen)
                }
            }

            is TopicsEvent.ToggleListOrderMenu -> {
                _state.update { it.copy(isOrderMenuExpanded = event.isExpanded) }
            }
        }
    }
}