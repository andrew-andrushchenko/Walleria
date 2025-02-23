package com.andrii_a.walleria.data.remote.source.topic

import com.andrii_a.walleria.data.remote.services.TopicService
import com.andrii_a.walleria.data.remote.source.base.BasePagingSource
import com.andrii_a.walleria.data.util.Config
import com.andrii_a.walleria.domain.TopicsDisplayOrder
import com.andrii_a.walleria.domain.models.topic.Topic
import com.andrii_a.walleria.domain.network.Resource
import kotlinx.coroutines.ensureActive
import kotlin.coroutines.coroutineContext

class TopicsPagingSource(
    private val topicService: TopicService,
    private val order: TopicsDisplayOrder
) : BasePagingSource<Topic>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Topic> {
        val pageKey = params.key ?: Config.INITIAL_PAGE_INDEX

        return try {
            val result = topicService.getTopics(
                page = pageKey,
                perPage = Config.PAGE_SIZE,
                orderBy = order.value
            )

            val topics: List<Topic> = when (result) {
                is Resource.Empty, is Resource.Loading -> emptyList()
                is Resource.Error -> throw result.asException()
                is Resource.Success -> result.value.map { it.toTopic() }
            }

            LoadResult.Page(
                data = topics,
                prevKey = if (pageKey == Config.INITIAL_PAGE_INDEX) null else pageKey - 1,
                nextKey = if (topics.isEmpty()) null else pageKey + 1
            )
        } catch (exception: Exception) {
            coroutineContext.ensureActive()
            LoadResult.Error(exception)
        }
    }
}