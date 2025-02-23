package com.andrii_a.walleria.data.remote.services

import com.andrii_a.walleria.data.remote.dto.photo.PhotoDto
import com.andrii_a.walleria.domain.network.Resource

interface PhotoService {

    suspend fun getPhotos(
        page: Int?,
        perPage: Int?,
        orderBy: String?
    ): Resource<List<PhotoDto>>

    suspend fun getCollectionPhotos(
        id: String,
        page: Int?,
        perPage: Int?
    ): Resource<List<PhotoDto>>

    suspend fun getUserPhotos(
        username: String,
        page: Int?,
        perPage: Int?,
        orderBy: String?,
        stats: Boolean?,
        resolution: String?,
        quantity: Int?,
        orientation: String?
    ): Resource<List<PhotoDto>>

    suspend fun getUserLikedPhotos(
        username: String,
        page: Int?,
        perPage: Int?,
        orderBy: String?,
        orientation: String?
    ): Resource<List<PhotoDto>>

    suspend fun getTopicPhotos(
        idOrSlug: String,
        page: Int?,
        perPage: Int?,
        orientation: String?,
        orderBy: String?
    ): Resource<List<PhotoDto>>

    suspend fun getPhoto(id: String): Resource<PhotoDto>

    suspend fun likePhoto(id: String): Resource<Unit>

    suspend fun dislikePhoto(id: String): Resource<Unit>

    suspend fun trackDownload(photoId: String): Resource<Unit>
}