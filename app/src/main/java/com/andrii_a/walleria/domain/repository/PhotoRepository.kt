package com.andrii_a.walleria.domain.repository

import androidx.paging.PagingData
import com.andrii_a.walleria.domain.PhotoListDisplayOrder
import com.andrii_a.walleria.domain.TopicPhotosOrientation
import com.andrii_a.walleria.domain.models.photo.Photo
import com.andrii_a.walleria.domain.network.Resource
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

    fun getPhoto(photoId: String): Flow<Resource<Photo>>

    fun likePhoto(id: String): Flow<Resource<Unit>>

    fun dislikePhoto(id: String): Flow<Resource<Unit>>

    fun getUserCollectionIdsForPhoto(photoId: String): Flow<Resource<List<String>>>

    suspend fun trackPhotoDownload(photoId: String)
}
