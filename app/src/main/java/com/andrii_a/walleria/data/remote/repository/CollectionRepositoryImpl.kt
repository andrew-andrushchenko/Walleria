package com.andrii_a.walleria.data.remote.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.andrii_a.walleria.core.BackendResult
import com.andrii_a.walleria.data.remote.source.collection.CollectionsPagingSource
import com.andrii_a.walleria.data.remote.source.collection.CollectionsService
import com.andrii_a.walleria.data.remote.source.collection.UserCollectionsPagingSource
import com.andrii_a.walleria.data.util.PAGE_SIZE
import com.andrii_a.walleria.data.util.network.backendRequest
import com.andrii_a.walleria.data.util.network.backendRequestFlow
import com.andrii_a.walleria.domain.models.collect_photo.CollectionPhotoResult
import com.andrii_a.walleria.domain.models.collection.Collection
import com.andrii_a.walleria.domain.repository.CollectionRepository
import kotlinx.coroutines.flow.Flow

class CollectionRepositoryImpl(private val collectionsService: CollectionsService) :
    CollectionRepository {

    override fun getCollections(): Flow<PagingData<Collection>> =
        Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { CollectionsPagingSource(collectionsService) }
        ).flow

    override fun getCollection(id: String): Flow<BackendResult<Collection>> =
        backendRequestFlow {
            collectionsService.getCollection(id).toCollection()
        }

    override fun getUserCollections(username: String): Flow<PagingData<Collection>> =
        Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { UserCollectionsPagingSource(collectionsService, username) }
        ).flow

    override suspend fun createCollection(
        title: String,
        description: String?,
        isPrivate: Boolean?
    ): BackendResult<Collection> = backendRequest {
        collectionsService.createCollection(
            title,
            description,
            isPrivate
        ).toCollection()
    }

    override suspend fun updateCollection(
        id: String,
        title: String?,
        description: String?,
        isPrivate: Boolean
    ): BackendResult<Collection> = backendRequest {
        collectionsService.updateCollection(
            id,
            title,
            description,
            isPrivate
        ).toCollection()
    }

    override suspend fun deleteCollection(id: String): BackendResult<Unit> = backendRequest {
        collectionsService.deleteCollection(id)
    }

    override suspend fun addPhotoToCollection(
        collectionId: String,
        photoId: String
    ): BackendResult<CollectionPhotoResult> = backendRequest {
        collectionsService.addPhotoToCollection(
            collectionId,
            photoId
        ).toCollectionPhotoResult()
    }

    override suspend fun deletePhotoFromCollection(
        collectionId: String,
        photoId: String
    ): BackendResult<CollectionPhotoResult> = backendRequest {
        collectionsService.deletePhotoFromCollection(
            collectionId,
            photoId
        ).toCollectionPhotoResult()
    }

    override suspend fun getRelatedCollection(id: String): BackendResult<List<Collection>> =
        backendRequest {
            collectionsService.getRelatedCollections(id).map { it.toCollection() }
        }
}