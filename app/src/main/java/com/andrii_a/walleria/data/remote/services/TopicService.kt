package com.andrii_a.walleria.data.remote.services

import com.andrii_a.walleria.data.remote.dto.topic.TopicDto
import com.andrii_a.walleria.domain.network.Resource

interface TopicService {

    suspend fun getTopics(
        page: Int?,
        perPage: Int?,
        orderBy: String?
    ): Resource<List<TopicDto>>

    suspend fun getTopic(idOrSlug: String): Resource<TopicDto>
}