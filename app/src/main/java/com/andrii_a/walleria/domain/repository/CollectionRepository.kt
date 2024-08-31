package com.andrii_a.walleria.domain.repository

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import com.andrii_a.walleria.domain.network.Resource
import com.andrii_a.walleria.domain.models.collect_photo.CollectionPhotoResult
import com.andrii_a.walleria.domain.models.collection.Collection

interface CollectionRepository {

    fun getCollections(): Flow<PagingData<Collection>>

    fun getCollection(id: String): Flow<Resource<Collection>>

    fun getUserCollections(username: String): Flow<PagingData<Collection>>

    suspend fun createCollection(
        title: String,
        description: String?,
        isPrivate: Boolean?
    ): Resource<Collection>

    suspend fun updateCollection(
        id: String,
        title: String?,
        description: String?,
        isPrivate: Boolean
    ): Resource<Collection>

    suspend fun deleteCollection(id: String): Resource<Unit>

    suspend fun addPhotoToCollection(
        collectionId: String,
        photoId: String
    ): Resource<CollectionPhotoResult>

    suspend fun deletePhotoFromCollection(
        collectionId: String,
        photoId: String
    ): Resource<CollectionPhotoResult>

    suspend fun getRelatedCollection(id: String): Resource<List<Collection>>
}