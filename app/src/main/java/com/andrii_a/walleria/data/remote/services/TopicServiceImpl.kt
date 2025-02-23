package com.andrii_a.walleria.data.remote.services

import com.andrii_a.walleria.data.remote.dto.topic.TopicDto
import com.andrii_a.walleria.data.util.Endpoints
import com.andrii_a.walleria.data.util.backendRequest
import com.andrii_a.walleria.domain.network.Resource
import io.ktor.client.HttpClient
import io.ktor.client.request.get

class TopicServiceImpl(private val httpClient: HttpClient) : TopicService {

    override suspend fun getTopics(
        page: Int?,
        perPage: Int?,
        orderBy: String?
    ): Resource<List<TopicDto>> {
        return backendRequest {
            httpClient.get(Endpoints.TOPICS) {
                url {
                    parameters.append("page", page.toString())
                    parameters.append("per_page", perPage.toString())
                    parameters.append("order_by", orderBy.orEmpty())
                }
            }
        }
    }

    override suspend fun getTopic(idOrSlug: String): Resource<TopicDto> {
        return backendRequest {
            httpClient.get(Endpoints.singleTopic(idOrSlug))
        }
    }
}