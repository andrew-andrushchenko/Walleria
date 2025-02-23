package com.andrii_a.walleria.data.remote.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.andrii_a.walleria.data.remote.services.CollectionsService
import com.andrii_a.walleria.data.remote.source.collection.CollectionsPagingSource
import com.andrii_a.walleria.data.remote.source.collection.UserCollectionsPagingSource
import com.andrii_a.walleria.data.util.Config
import com.andrii_a.walleria.domain.models.collect_photo.CollectionPhotoResult
import com.andrii_a.walleria.domain.models.collection.Collection
import com.andrii_a.walleria.domain.network.Resource
import com.andrii_a.walleria.domain.repository.CollectionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class CollectionRepositoryImpl(private val collectionsService: CollectionsService) :
    CollectionRepository {

    override fun getCollections(): Flow<PagingData<Collection>> =
        Pager(
            config = PagingConfig(
                pageSize = Config.PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { CollectionsPagingSource(collectionsService) }
        ).flow

    override fun getCollection(id: String): Flow<Resource<Collection>> = flow {
        emit(Resource.Loading)

        when (val result = collectionsService.getCollection(id)) {
            is Resource.Error -> emit(result)
            is Resource.Success -> emit(Resource.Success(result.value.toCollection()))
            else -> Unit
        }
    }

    override fun getUserCollections(username: String): Flow<PagingData<Collection>> =
        Pager(
            config = PagingConfig(
                pageSize = Config.PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { UserCollectionsPagingSource(collectionsService, username) }
        ).flow

    override fun createCollection(
        title: String,
        description: String?,
        isPrivate: Boolean?
    ): Flow<Resource<Collection>> = flow {
        emit(Resource.Loading)

        val result = collectionsService.createCollection(
            title = title,
            description = description,
            isPrivate = isPrivate
        )

        when (result) {
            is Resource.Error -> emit(result)
            is Resource.Success -> emit(Resource.Success(result.value.toCollection()))
            else -> Unit
        }
    }

    override fun updateCollection(
        id: String,
        title: String?,
        description: String?,
        isPrivate: Boolean
    ): Flow<Resource<Collection>> = flow {
        emit(Resource.Loading)

        val result = collectionsService.updateCollection(
            id = id,
            title = title,
            description = description,
            isPrivate = isPrivate
        )

        when (result) {
            is Resource.Error -> emit(result)
            is Resource.Success -> emit(Resource.Success(result.value.toCollection()))
            else -> Unit

        }
    }

    override fun deleteCollection(id: String): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading)

        when (val result = collectionsService.deleteCollection(id)) {
            is Resource.Error -> emit(result)
            is Resource.Success -> emit(Resource.Success(Unit))
            else -> Unit
        }
    }

    override fun addPhotoToCollection(
        collectionId: String,
        photoId: String
    ): Flow<Resource<CollectionPhotoResult>> = flow {
        emit(Resource.Loading)

        when (val result = collectionsService.addPhotoToCollection(collectionId, photoId)) {
            is Resource.Error -> emit(result)
            is Resource.Success -> emit(Resource.Success(result.value.toCollectionPhotoResult()))
            else -> Unit
        }
    }

    override fun deletePhotoFromCollection(
        collectionId: String,
        photoId: String
    ): Flow<Resource<CollectionPhotoResult>> = flow {
        emit(Resource.Loading)

        when (val result = collectionsService.deletePhotoFromCollection(collectionId, photoId)) {
            is Resource.Error -> emit(result)
            is Resource.Success -> emit(Resource.Success(result.value.toCollectionPhotoResult()))
            else -> Unit
        }
    }

    override fun getRelatedCollection(id: String): Flow<Resource<List<Collection>>> = flow {
        emit(Resource.Loading)

        when (val result = collectionsService.getRelatedCollections(id)) {
            is Resource.Error -> emit(result)
            is Resource.Success -> emit(Resource.Success(result.value.map { it.toCollection() }))
            else -> Unit
        }
    }
}