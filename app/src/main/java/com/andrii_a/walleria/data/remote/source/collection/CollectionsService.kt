package com.andrii_a.walleria.data.remote.source.collection

import com.andrii_a.walleria.data.remote.dto.add_photo_to_collection.CollectionPhotoResultDTO
import com.andrii_a.walleria.data.remote.dto.collection.CollectionDTO
import retrofit2.Response
import retrofit2.http.*

interface CollectionsService {

    @GET("collections")
    suspend fun getCollections(
        @Query("page") page: Int?,
        @Query("per_page") perPage: Int?
    ): List<CollectionDTO>

    @GET("users/{username}/collections")
    suspend fun getUserCollections(
        @Path("username") username: String,
        @Query("page") page: Int?,
        @Query("per_page") perPage: Int?
    ): List<CollectionDTO>

    @POST("collections")
    suspend fun createCollection(
        @Query("title") title: String,
        @Query("description") description: String?,
        @Query("private") isPrivate: Boolean?
    ): CollectionDTO

    @PUT("collections/{id}")
    suspend fun updateCollection(
        @Path("id") id: String,
        @Query("title") title: String?,
        @Query("description") description: String?,
        @Query("private") isPrivate: Boolean?
    ): CollectionDTO

    @DELETE("collections/{id}")
    suspend fun deleteCollection(
        @Path("id") id: String
    ): Response<Unit>

    @POST("collections/{collection_id}/add")
    suspend fun addPhotoToCollection(
        @Path("collection_id") collectionId: String,
        @Query("photo_id") photoId: String
    ): CollectionPhotoResultDTO

    @DELETE("collections/{collection_id}/remove")
    suspend fun deletePhotoFromCollection(
        @Path("collection_id") collectionId: String,
        @Query("photo_id") photoId: String
    ): CollectionPhotoResultDTO

    @GET("collections/{id}/related")
    suspend fun getRelatedCollections(
        @Path("id") id: String
    ): List<CollectionDTO>
}