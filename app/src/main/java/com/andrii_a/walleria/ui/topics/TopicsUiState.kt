package com.andrii_a.walleria.ui.topics

import androidx.paging.PagingData
import com.andrii_a.walleria.domain.TopicsDisplayOrder
import com.andrii_a.walleria.domain.models.topic.Topic

data class TopicsUiState(
    val topicsPagingData: PagingData<Topic> = PagingData.empty(),
    val topicsDisplayOrder: TopicsDisplayOrder = TopicsDisplayOrder.LATEST
)
