package com.andrii_a.walleria.data.remote.services

import com.andrii_a.walleria.data.remote.dto.collect_photo.CollectionPhotoResultDto
import com.andrii_a.walleria.data.remote.dto.collection.CollectionDto
import com.andrii_a.walleria.domain.network.Resource

interface CollectionsService {

    suspend fun getCollections(
        page: Int?,
        perPage: Int?
    ): Resource<List<CollectionDto>>

    suspend fun getCollection(id: String): Resource<CollectionDto>

    suspend fun getUserCollections(
        username: String,
        page: Int?,
        perPage: Int?
    ): Resource<List<CollectionDto>>

    suspend fun createCollection(
        title: String,
        description: String?,
        isPrivate: Boolean?
    ): Resource<CollectionDto>

    suspend fun updateCollection(
        id: String,
        title: String?,
        description: String?,
        isPrivate: Boolean?
    ): Resource<CollectionDto>

    suspend fun deleteCollection(id: String): Resource<Unit>

    suspend fun addPhotoToCollection(
        collectionId: String,
        photoId: String
    ): Resource<CollectionPhotoResultDto>

    suspend fun deletePhotoFromCollection(
        collectionId: String,
        photoId: String
    ): Resource<CollectionPhotoResultDto>

    suspend fun getRelatedCollections(id: String): Resource<List<CollectionDto>>
}