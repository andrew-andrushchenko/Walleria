package com.andrii_a.walleria.data.remote.services

import com.andrii_a.walleria.data.remote.dto.photo.PhotoDTO
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface PhotoService {

    @GET("photos")
    suspend fun getPhotos(
        @Query("page") page: Int?,
        @Query("per_page") perPage: Int?,
        @Query("order_by") orderBy: String?
    ): List<PhotoDTO>

    @GET("collections/{id}/photos")
    suspend fun getCollectionPhotos(
        @Path("id") id: String,
        @Query("page") page: Int?,
        @Query("per_page") perPage: Int?
    ): List<PhotoDTO>

    @GET("users/{username}/photos")
    suspend fun getUserPhotos(
        @Path("username") username: String,
        @Query("page") page: Int?,
        @Query("per_page") perPage: Int?,
        @Query("order_by") orderBy: String?,
        @Query("stats") stats: Boolean?,
        @Query("resolution") resolution: String?,
        @Query("quantity") quantity: Int?,
        @Query("orientation") orientation: String?
    ): List<PhotoDTO>

    @GET("users/{username}/likes")
    suspend fun getUserLikedPhotos(
        @Path("username") username: String,
        @Query("page") page: Int?,
        @Query("per_page") perPage: Int?,
        @Query("order_by") orderBy: String?,
        @Query("orientation") orientation: String?
    ): List<PhotoDTO>

    @GET("topics/{id_or_slug}/photos")
    suspend fun getTopicPhotos(
        @Path("id_or_slug") idOrSlug: String,
        @Query("page") page: Int?,
        @Query("per_page") perPage: Int?,
        @Query("orientation") orientation: String?,
        @Query("order_by") orderBy: String?
    ): List<PhotoDTO>

    @GET("photos/{id}")
    suspend fun getPhoto(
        @Path("id") id: String
    ): PhotoDTO

    @GET("photos/random")
    suspend fun getRandomPhotos(
        @Query("collections") collectionId: String?,
        @Query("featured") featured: Boolean?,
        @Query("username") username: String?,
        @Query("query") query: String?,
        @Query("orientation") orientation: String?,
        @Query("content_filter") contentFilter: String?,
        @Query("count") count: Int?
    ): List<PhotoDTO>

    @POST("photos/{id}/like")
    suspend fun likePhoto(
        @Path("id") id: String,
    ): ResponseBody

    @DELETE("photos/{id}/like")
    suspend fun dislikePhoto(
        @Path("id") id: String
    ): Response<Unit>

    @GET("photos/{id}/download")
    suspend fun trackDownload(@Path("id") photoId: String): ResponseBody
}