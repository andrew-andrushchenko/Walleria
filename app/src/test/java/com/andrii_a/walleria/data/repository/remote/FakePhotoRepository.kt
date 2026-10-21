package com.andrii_a.walleria.data.repository.remote

import androidx.paging.PagingData
import com.andrii_a.walleria.domain.PhotoListDisplayOrder
import com.andrii_a.walleria.domain.TopicPhotosOrientation
import com.andrii_a.walleria.domain.models.photo.Photo
import com.andrii_a.walleria.domain.network.Resource
import com.andrii_a.walleria.domain.repository.PhotoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf

class FakePhotoRepository(
    initialPhotos: List<Photo> = emptyList(),
    var photoToReturn: Photo? = null,
    var shouldReturnError: Boolean = false,
    var errorMessage: String = "Error occurred",
    userCollectionIdsMap: Map<String, List<String>> = emptyMap()
) : PhotoRepository {

    val photos = initialPhotos.toMutableList()
    val userCollectionIdsMap = userCollectionIdsMap.toMutableMap()

    var lastRequestedOrder: PhotoListDisplayOrder? = null
    var lastRequestedCollectionId: String? = null
    var lastRequestedUsername: String? = null
    var lastRequestedUserLikedUsername: String? = null
    var lastRequestedTopicIdOrSlug: String? = null
    var lastRequestedTopicOrientation: TopicPhotosOrientation? = null
    var lastRequestedTopicOrder: PhotoListDisplayOrder? = null

    var lastRequestedPhotoId: String? = null
    var lastGetUserCollectionIdsPhotoId: String? = null

    val likedPhotoIds = mutableListOf<String>()
    val dislikedPhotoIds = mutableListOf<String>()
    val trackedDownloadPhotoIds = mutableListOf<String>()

    override fun getPhotos(order: PhotoListDisplayOrder): Flow<PagingData<Photo>> {
        lastRequestedOrder = order
        return flowOf(PagingData.from(photos))
    }

    override fun getCollectionPhotos(collectionId: String): Flow<PagingData<Photo>> {
        lastRequestedCollectionId = collectionId
        return flowOf(PagingData.from(photos))
    }

    override fun getUserPhotos(username: String): Flow<PagingData<Photo>> {
        lastRequestedUsername = username
        return flowOf(PagingData.from(photos))
    }

    override fun getUserLikedPhotos(username: String): Flow<PagingData<Photo>> {
        lastRequestedUserLikedUsername = username
        return flowOf(PagingData.from(photos))
    }

    override fun getTopicPhotos(
        idOrSlug: String,
        orientation: TopicPhotosOrientation,
        order: PhotoListDisplayOrder
    ): Flow<PagingData<Photo>> {
        lastRequestedTopicIdOrSlug = idOrSlug
        lastRequestedTopicOrientation = orientation
        lastRequestedTopicOrder = order
        return flowOf(PagingData.from(photos))
    }

    override fun getPhoto(photoId: String): Flow<Resource<Photo>> = flow {
        lastRequestedPhotoId = photoId
        emit(Resource.Loading)
        if (shouldReturnError) {
            emit(Resource.Error(reason = errorMessage))
        } else {
            val photo = photos.find { it.id == photoId } ?: photoToReturn
            if (photo != null) {
                emit(Resource.Success(photo))
            } else {
                emit(Resource.Error(reason = "Photo not found"))
            }
        }
    }

    override fun likePhoto(id: String): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading)
        if (shouldReturnError) {
            emit(Resource.Error(reason = errorMessage))
        } else {
            likedPhotoIds.add(id)
            dislikedPhotoIds.remove(id)
            emit(Resource.Success(Unit))
        }
    }

    override fun dislikePhoto(id: String): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading)
        if (shouldReturnError) {
            emit(Resource.Error(reason = errorMessage))
        } else {
            dislikedPhotoIds.add(id)
            likedPhotoIds.remove(id)
            emit(Resource.Success(Unit))
        }
    }

    override fun getUserCollectionIdsForPhoto(photoId: String): Flow<Resource<List<String>>> = flow {
        lastGetUserCollectionIdsPhotoId = photoId
        emit(Resource.Loading)
        if (shouldReturnError) {
            emit(Resource.Error(reason = errorMessage))
        } else {
            val collectionIds = userCollectionIdsMap[photoId] ?: emptyList()
            emit(Resource.Success(collectionIds))
        }
    }

    override suspend fun trackPhotoDownload(photoId: String) {
        trackedDownloadPhotoIds.add(photoId)
    }
}