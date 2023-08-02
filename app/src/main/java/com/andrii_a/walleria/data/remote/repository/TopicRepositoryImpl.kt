package com.andrii_a.walleria.data.remote.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.andrii_a.walleria.core.TopicsDisplayOrder
import com.andrii_a.walleria.data.remote.service.TopicService
import com.andrii_a.walleria.data.remote.source.topic.TopicsPagingSource
import com.andrii_a.walleria.data.util.PAGE_SIZE
import com.andrii_a.walleria.core.BackendResult
import com.andrii_a.walleria.data.util.network.backendRequest
import com.andrii_a.walleria.domain.models.topic.Topic
import com.andrii_a.walleria.domain.repository.TopicRepository
import kotlinx.coroutines.flow.Flow

class TopicRepositoryImpl(private val topicService: TopicService) : TopicRepository {

    override fun getTopics(order: TopicsDisplayOrder): Flow<PagingData<Topic>> =
        Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { TopicsPagingSource(topicService, order) }
        ).flow

    override suspend fun getTopic(idOrSlug: String): BackendResult<Topic> = backendRequest {
        topicService.getTopic(idOrSlug).toTopic()
    }
}