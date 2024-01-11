package com.andrii_a.walleria.ui.topics

import androidx.paging.PagingData
import com.andrii_a.walleria.domain.TopicsDisplayOrder
import com.andrii_a.walleria.domain.models.topic.Topic
import com.andrii_a.walleria.ui.util.emptyPagingDataFlow
import kotlinx.coroutines.flow.Flow

data class TopicsUiState(
    val topics: Flow<PagingData<Topic>> = emptyPagingDataFlow(),
    val topicsDisplayOrder: TopicsDisplayOrder = TopicsDisplayOrder.LATEST
)
