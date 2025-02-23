package com.andrii_a.walleria.data.remote.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.andrii_a.walleria.data.remote.services.TopicService
import com.andrii_a.walleria.data.remote.source.topic.TopicsPagingSource
import com.andrii_a.walleria.data.util.Config
import com.andrii_a.walleria.domain.TopicsDisplayOrder
import com.andrii_a.walleria.domain.models.topic.Topic
import com.andrii_a.walleria.domain.network.Resource
import com.andrii_a.walleria.domain.repository.TopicRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TopicRepositoryImpl(private val topicService: TopicService) : TopicRepository {

    override fun getTopics(order: TopicsDisplayOrder): Flow<PagingData<Topic>> =
        Pager(
            config = PagingConfig(
                pageSize = Config.PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { TopicsPagingSource(topicService, order) }
        ).flow

    override fun getTopic(idOrSlug: String): Flow<Resource<Topic>> = flow {
        emit(Resource.Loading)

        when (val result = topicService.getTopic(idOrSlug)) {
            is Resource.Error -> emit(result)
            is Resource.Success -> emit(Resource.Success(result.value.toTopic()))
            else -> Unit
        }
    }
}