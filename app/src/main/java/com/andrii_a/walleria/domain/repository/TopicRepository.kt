package com.andrii_a.walleria.domain.repository

import androidx.paging.PagingData
import com.andrii_a.walleria.core.TopicsOrder
import com.andrii_a.walleria.data.util.network.BackendResult
import com.andrii_a.walleria.domain.models.topic.Topic
import kotlinx.coroutines.flow.Flow

interface TopicRepository {

    fun getTopics(order: TopicsOrder): Flow<PagingData<Topic>>

    suspend fun getTopic(idOrSlug: String): BackendResult<Topic>

}