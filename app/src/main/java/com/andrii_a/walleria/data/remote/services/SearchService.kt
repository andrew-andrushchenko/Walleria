package com.andrii_a.walleria.data.remote.services

import com.andrii_a.walleria.data.remote.dto.search.SearchCollectionsResultDTO
import com.andrii_a.walleria.data.remote.dto.search.SearchPhotosResultDTO
import com.andrii_a.walleria.data.remote.dto.search.SearchUsersResultDTO
import retrofit2.http.GET
import retrofit2.http.Query

interface SearchService {

    @GET("search/photos")
    suspend fun searchPhotos(
        @Query("query") query: String,
        @Query("page") page: Int?,
        @Query("per_page") perPage: Int?,
        @Query("order_by") orderBy: String?,
        @Query("collections") collections: String?,
        @Query("content_filter") contentFilter: String?,
        @Query("color") color: String?,
        @Query("orientation") orientation: String?
    ): SearchPhotosResultDTO

    @GET("search/collections")
    suspend fun searchCollections(
        @Query("query") query: String,
        @Query("page") page: Int?,
        @Query("per_page") perPage: Int?
    ): SearchCollectionsResultDTO

    @GET("search/users")
    suspend fun searchUsers(
        @Query("query") query: String,
        @Query("page") page: Int?,
        @Query("per_page") perPage: Int?
    ): SearchUsersResultDTO
}