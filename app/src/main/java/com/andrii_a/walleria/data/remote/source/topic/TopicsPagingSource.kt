package com.andrii_a.walleria.data.remote.source.topic

import com.andrii_a.walleria.domain.TopicsDisplayOrder
import com.andrii_a.walleria.data.remote.services.TopicService
import com.andrii_a.walleria.data.remote.source.base.BasePagingSource
import com.andrii_a.walleria.data.util.INITIAL_PAGE_INDEX
import com.andrii_a.walleria.data.util.PAGE_SIZE
import com.andrii_a.walleria.domain.models.topic.Topic
import retrofit2.HttpException
import java.io.IOException

class TopicsPagingSource(
    private val topicService: TopicService,
    private val order: TopicsDisplayOrder
) : BasePagingSource<Topic>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Topic> {
        val pageKey = params.key ?: INITIAL_PAGE_INDEX

        return try {
            val topics: List<Topic> = topicService.getTopics(
                pageKey,
                PAGE_SIZE,
                order.value
            ).map { it.toTopic() }

            LoadResult.Page(
                data = topics,
                prevKey = if (pageKey == INITIAL_PAGE_INDEX) null else pageKey - 1,
                nextKey = if (topics.isEmpty()) null else pageKey + 1
            )
        } catch (exception: IOException) {
            LoadResult.Error(exception)
        } catch (exception: HttpException) {
            LoadResult.Error(exception)
        }
    }
}