package com.andrii_a.walleria.domain.repository

import androidx.paging.PagingData
import com.andrii_a.walleria.core.TopicsDisplayOrder
import com.andrii_a.walleria.core.BackendResult
import com.andrii_a.walleria.domain.models.topic.Topic
import kotlinx.coroutines.flow.Flow

interface TopicRepository {

    fun getTopics(order: TopicsDisplayOrder): Flow<PagingData<Topic>>

    suspend fun getTopic(idOrSlug: String): BackendResult<Topic>

}