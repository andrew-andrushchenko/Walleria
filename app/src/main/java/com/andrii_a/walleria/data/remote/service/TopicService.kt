package com.andrii_a.walleria.data.remote.service

import com.andrii_a.walleria.data.remote.dto.topic.TopicDTO
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TopicService {

    @GET("topics")
    suspend fun getTopics(
        @Query("page") page: Int?,
        @Query("per_page") perPage: Int?,
        @Query("order_by") orderBy: String?
    ): List<TopicDTO>

    @GET("topics/{id_or_slug}")
    suspend fun getTopic(
        @Path("id_or_slug") idOrSlug: String
    ): TopicDTO
}