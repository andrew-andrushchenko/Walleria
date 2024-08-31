package com.andrii_a.walleria.domain.repository

import androidx.paging.PagingData
import com.andrii_a.walleria.domain.TopicsDisplayOrder
import com.andrii_a.walleria.domain.network.Resource
import com.andrii_a.walleria.domain.models.topic.Topic
import kotlinx.coroutines.flow.Flow

interface TopicRepository {

    fun getTopics(order: TopicsDisplayOrder): Flow<PagingData<Topic>>

    fun getTopic(idOrSlug: String): Flow<Resource<Topic>>

}