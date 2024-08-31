package com.andrii_a.walleria.domain.repository

import androidx.paging.PagingData
import com.andrii_a.walleria.domain.PhotoListDisplayOrder
import com.andrii_a.walleria.domain.SearchResultsContentFilter
import com.andrii_a.walleria.domain.SearchResultsPhotoOrientation
import com.andrii_a.walleria.domain.TopicPhotosOrientation
import com.andrii_a.walleria.domain.network.Resource
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

    fun getPhoto(photoId: String): Flow<Resource<Photo>>

    fun getRandomPhoto(
        collectionId: String? = null,
        featured: Boolean = false,
        username: String? = null,
        query: String? = null,
        orientation: SearchResultsPhotoOrientation = SearchResultsPhotoOrientation.ANY,
        contentFilter: SearchResultsContentFilter = SearchResultsContentFilter.LOW
    ): Flow<Resource<Photo>>

    suspend fun likePhoto(id: String): Resource<Unit>

    suspend fun dislikePhoto(id: String): Resource<Unit>

    suspend fun getUserCollectionIdsForPhoto(photoId: String): List<String>

    suspend fun trackPhotoDownload(photoId: String)
}
