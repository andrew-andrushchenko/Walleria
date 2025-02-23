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

    fun createCollection(
        title: String,
        description: String?,
        isPrivate: Boolean?
    ): Flow<Resource<Collection>>

    fun updateCollection(
        id: String,
        title: String?,
        description: String?,
        isPrivate: Boolean
    ): Flow<Resource<Collection>>

    fun deleteCollection(id: String): Flow<Resource<Unit>>

    fun addPhotoToCollection(
        collectionId: String,
        photoId: String
    ): Flow<Resource<CollectionPhotoResult>>

    fun deletePhotoFromCollection(
        collectionId: String,
        photoId: String
    ): Flow<Resource<CollectionPhotoResult>>

    fun getRelatedCollection(id: String): Flow<Resource<List<Collection>>>
}