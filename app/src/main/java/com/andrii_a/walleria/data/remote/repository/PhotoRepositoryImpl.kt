package com.andrii_a.walleria.data.remote.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.andrii_a.walleria.domain.network.BackendResult
import com.andrii_a.walleria.domain.PhotoListDisplayOrder
import com.andrii_a.walleria.domain.TopicPhotosOrientation
import com.andrii_a.walleria.data.remote.services.PhotoService
import com.andrii_a.walleria.data.remote.source.photo.CollectionPhotosPagingSource
import com.andrii_a.walleria.data.remote.source.photo.PhotosPagingSource
import com.andrii_a.walleria.data.remote.source.photo.TopicPhotosPagingSource
import com.andrii_a.walleria.data.remote.source.photo.UserLikedPhotosPagingSource
import com.andrii_a.walleria.data.remote.source.photo.UserPhotosPagingSource
import com.andrii_a.walleria.data.util.PAGE_SIZE
import com.andrii_a.walleria.data.util.network.backendRequest
import com.andrii_a.walleria.data.util.network.backendRequestFlow
import com.andrii_a.walleria.domain.SearchResultsContentFilter
import com.andrii_a.walleria.domain.SearchResultsPhotoOrientation
import com.andrii_a.walleria.domain.models.photo.Photo
import com.andrii_a.walleria.domain.repository.PhotoRepository
import kotlinx.coroutines.flow.Flow

class PhotoRepositoryImpl(private val photoService: PhotoService) : PhotoRepository {

    override fun getPhotos(order: PhotoListDisplayOrder): Flow<PagingData<Photo>> =
        Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { PhotosPagingSource(photoService, order) }
        ).flow

    override fun getCollectionPhotos(collectionId: String): Flow<PagingData<Photo>> =
        Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { CollectionPhotosPagingSource(photoService, collectionId) }
        ).flow

    override fun getUserPhotos(username: String): Flow<PagingData<Photo>> =
        Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { UserPhotosPagingSource(photoService, username) }
        ).flow

    override fun getUserLikedPhotos(username: String): Flow<PagingData<Photo>> =
        Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
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
                pageSize = PAGE_SIZE,
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

    override fun getPhoto(photoId: String): Flow<BackendResult<Photo>> =
        backendRequestFlow {
            photoService.getPhoto(photoId).toPhoto()
        }

    override fun getRandomPhoto(
        collectionId: String?,
        featured: Boolean,
        username: String?,
        query: String?,
        orientation: SearchResultsPhotoOrientation,
        contentFilter: SearchResultsContentFilter
    ): Flow<BackendResult<Photo>> = backendRequestFlow {
        photoService.getRandomPhotos(
            collectionId = collectionId,
            featured = featured,
            username = username,
            query = query,
            orientation = orientation.value,
            contentFilter = contentFilter.value,
            count = 1
        ).first().toPhoto()
    }

    override suspend fun likePhoto(id: String): BackendResult<Unit> = backendRequest {
        photoService.likePhoto(id)
    }

    override suspend fun dislikePhoto(id: String): BackendResult<Unit> = backendRequest {
        photoService.dislikePhoto(id)
    }

    override suspend fun getUserCollectionIdsForPhoto(photoId: String): List<String> {
        val result = backendRequest {
            val photo = photoService.getPhoto(photoId).toPhoto()
            photo.currentUserCollections?.map { it.id }?.toList()
        }

        return when (result) {
            is BackendResult.Empty, is BackendResult.Loading, is BackendResult.Error -> emptyList()
            is BackendResult.Success -> result.value ?: emptyList()
        }
    }

    override suspend fun trackPhotoDownload(photoId: String) {
        backendRequest {
            photoService.trackDownload(photoId)
        }
    }
}