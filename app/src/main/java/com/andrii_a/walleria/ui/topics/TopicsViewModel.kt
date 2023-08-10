package com.andrii_a.walleria.ui.topics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.andrii_a.walleria.domain.TopicsDisplayOrder
import com.andrii_a.walleria.domain.repository.TopicRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class TopicsViewModel @Inject constructor(
    private val topicRepository: TopicRepository,
) : ViewModel() {

    private val _order: MutableStateFlow<TopicsDisplayOrder> = MutableStateFlow(TopicsDisplayOrder.LATEST)
    val order: StateFlow<TopicsDisplayOrder> = _order.asStateFlow()

    val topics = _order.flatMapLatest { order ->
        topicRepository.getTopics(order).cachedIn(viewModelScope)
    }

    fun orderBy(orderOptionOrdinalNum: Int) {
        _order.update { TopicsDisplayOrder.values()[orderOptionOrdinalNum] }
    }

}