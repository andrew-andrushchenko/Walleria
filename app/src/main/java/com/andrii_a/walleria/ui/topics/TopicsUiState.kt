package com.andrii_a.walleria.ui.topics

import androidx.compose.runtime.Stable
import androidx.paging.PagingData
import com.andrii_a.walleria.domain.TopicsDisplayOrder
import com.andrii_a.walleria.domain.models.topic.Topic
import com.andrii_a.walleria.ui.util.emptyPagingData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

@Stable
data class TopicsUiState(
    private val topicsPagingData: PagingData<Topic> = PagingData.empty(),
    val isOrderMenuExpanded: Boolean = false,
    val topicsDisplayOrder: TopicsDisplayOrder = TopicsDisplayOrder.LATEST
) {
    private val _topics: MutableStateFlow<PagingData<Topic>> = MutableStateFlow(emptyPagingData())
    val topics: StateFlow<PagingData<Topic>> = _topics.asStateFlow()

    init {
        _topics.update { topicsPagingData }
    }
}
