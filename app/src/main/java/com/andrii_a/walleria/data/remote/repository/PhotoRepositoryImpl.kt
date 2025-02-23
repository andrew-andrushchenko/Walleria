package com.andrii_a.walleria.data.remote.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.andrii_a.walleria.data.remote.services.PhotoService
import com.andrii_a.walleria.data.remote.source.photo.CollectionPhotosPagingSource
import com.andrii_a.walleria.data.remote.source.photo.PhotosPagingSource
import com.andrii_a.walleria.data.remote.source.photo.TopicPhotosPagingSource
import com.andrii_a.walleria.data.remote.source.photo.UserLikedPhotosPagingSource
import com.andrii_a.walleria.data.remote.source.photo.UserPhotosPagingSource
import com.andrii_a.walleria.data.util.Config
import com.andrii_a.walleria.domain.PhotoListDisplayOrder
import com.andrii_a.walleria.domain.TopicPhotosOrientation
import com.andrii_a.walleria.domain.models.photo.Photo
import com.andrii_a.walleria.domain.network.Resource
import com.andrii_a.walleria.domain.repository.PhotoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PhotoRepositoryImpl(private val photoService: PhotoService) : PhotoRepository {

    override fun getPhotos(order: PhotoListDisplayOrder): Flow<PagingData<Photo>> =
        Pager(
            config = PagingConfig(
                pageSize = Config.PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { PhotosPagingSource(photoService, order) }
        ).flow

    override fun getCollectionPhotos(collectionId: String): Flow<PagingData<Photo>> =
        Pager(
            config = PagingConfig(
                pageSize = Config.PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { CollectionPhotosPagingSource(photoService, collectionId) }
        ).flow

    override fun getUserPhotos(username: String): Flow<PagingData<Photo>> =
        Pager(
            config = PagingConfig(
                pageSize = Config.PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { UserPhotosPagingSource(photoService, username) }
        ).flow

    override fun getUserLikedPhotos(username: String): Flow<PagingData<Photo>> =
        Pager(
            config = PagingConfig(
                pageSize = Config.PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { UserLikedPhotosPagingSource(photoService, username) }
        ).flow

    override fun getTopicPhotos(
        idOrSlug: String,
        orientation: TopicPhotosOrientation,
        order: PhotoListDisplayOrder
    ): Flow<PagingData<Photo>> =
        Pager(
            config = PagingConfig(
                pageSize = Config.PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                TopicPhotosPagingSource(
                    photoService,
                    idOrSlug,
                    orientation,
                    order
                )
            }
        ).flow

    override fun getPhoto(photoId: String): Flow<Resource<Photo>> = flow {
        emit(Resource.Loading)

        when (val result = photoService.getPhoto(photoId)) {
            is Resource.Error -> emit(result)
            is Resource.Success -> emit(Resource.Success(result.value.toPhoto()))
            else -> Unit
        }
    }

    override fun likePhoto(id: String): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading)

        when (val result = photoService.likePhoto(id)) {
            is Resource.Error -> emit(result)
            is Resource.Success -> emit(Resource.Success(Unit))
            else -> Unit
        }
    }

    override fun dislikePhoto(id: String): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading)

        when (val result = photoService.dislikePhoto(id)) {
            is Resource.Error -> emit(result)
            is Resource.Success -> emit(Resource.Success(Unit))
            else -> Unit
        }
    }

    override fun getUserCollectionIdsForPhoto(photoId: String): Flow<Resource<List<String>>> =
        flow {
            emit(Resource.Loading)

            when (val result = photoService.getPhoto(photoId)) {
                is Resource.Error -> emit(Resource.Error(result.code))
                is Resource.Success -> {
                    val userCollectionsList = result.value.currentUserCollections?.map { it.id.orEmpty() }?.toList() ?: emptyList()
                    emit(Resource.Success(userCollectionsList))
                }
                else -> Unit
            }
        }

    override suspend fun trackPhotoDownload(photoId: String) {
        photoService.trackDownload(photoId)
    }
}