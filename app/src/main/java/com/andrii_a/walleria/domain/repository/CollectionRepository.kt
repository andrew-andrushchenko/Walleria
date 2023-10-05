package com.andrii_a.walleria.domain.repository

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import com.andrii_a.walleria.domain.network.BackendResult
import com.andrii_a.walleria.domain.models.collect_photo.CollectionPhotoResult
import com.andrii_a.walleria.domain.models.collection.Collection

interface CollectionRepository {

    fun getCollections(): Flow<PagingData<Collection>>

    fun getCollection(id: String): Flow<BackendResult<Collection>>

    fun getUserCollections(username: String): Flow<PagingData<Collection>>

    suspend fun createCollection(
        title: String,
        description: String?,
        isPrivate: Boolean?
    ): BackendResult<Collection>

    suspend fun updateCollection(
        id: String,
        title: String?,
        description: String?,
        isPrivate: Boolean
    ): BackendResult<Collection>

    suspend fun deleteCollection(id: String): BackendResult<Unit>

    suspend fun addPhotoToCollection(
        collectionId: String,
        photoId: String
    ): BackendResult<CollectionPhotoResult>

    suspend fun deletePhotoFromCollection(
        collectionId: String,
        photoId: String
    ): BackendResult<CollectionPhotoResult>

    suspend fun getRelatedCollection(id: String): BackendResult<List<Collection>>
}