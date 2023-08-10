package com.andrii_a.walleria.domain.repository

import androidx.paging.PagingData
import com.andrii_a.walleria.domain.PhotoListDisplayOrder
import com.andrii_a.walleria.domain.TopicPhotosOrientation
import com.andrii_a.walleria.core.BackendResult
import com.andrii_a.walleria.domain.models.photo.Photo
import kotlinx.coroutines.flow.Flow

interface PhotoRepository {

    fun getPhotos(order: PhotoListDisplayOrder = PhotoListDisplayOrder.LATEST): Flow<PagingData<Photo>>

    fun getCollectionPhotos(collectionId: String): Flow<PagingData<Photo>>

    fun getUserPhotos(username: String): Flow<PagingData<Photo>>

    fun getUserLikedPhotos(username: String): Flow<PagingData<Photo>>

    fun getTopicPhotos(
        idOrSlug: String,
        orientation: TopicPhotosOrientation = TopicPhotosOrientation.LANDSCAPE,
        order: PhotoListDisplayOrder = PhotoListDisplayOrder.LATEST
    ): Flow<PagingData<Photo>>

    fun getPhoto(photoId: String): Flow<BackendResult<Photo>>

    suspend fun likePhoto(id: String): BackendResult<Unit>

    suspend fun dislikePhoto(id: String): BackendResult<Unit>

    suspend fun getUserCollectionIdsForPhoto(photoId: String): List<String>

    suspend fun trackPhotoDownload(photoId: String)
}
