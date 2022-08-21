package com.andrii_a.walleria.domain.repository

import androidx.paging.PagingData
import com.andrii_a.walleria.core.PhotoListOrder
import com.andrii_a.walleria.core.TopicPhotosOrientation
import com.andrii_a.walleria.data.util.network.BackendResult
import com.andrii_a.walleria.domain.models.photo.Photo
import kotlinx.coroutines.flow.Flow

interface PhotoRepository {

    fun getPhotos(order: PhotoListOrder = PhotoListOrder.LATEST): Flow<PagingData<Photo>>

    fun getCollectionPhotos(collectionId: String): Flow<PagingData<Photo>>

    fun getUserPhotos(username: String): Flow<PagingData<Photo>>

    fun getUserLikedPhotos(username: String): Flow<PagingData<Photo>>

    fun getTopicPhotos(
        idOrSlug: String,
        orientation: TopicPhotosOrientation = TopicPhotosOrientation.LANDSCAPE,
        order: PhotoListOrder = PhotoListOrder.LATEST
    ): Flow<PagingData<Photo>>

    fun getPhoto(photoId: String): Flow<BackendResult<Photo>>

    suspend fun likePhoto(id: String): BackendResult<Unit>

    suspend fun dislikePhoto(id: String): BackendResult<Unit>

    suspend fun getUserCollectionIdsForPhoto(photoId: String): MutableList<String>
}
