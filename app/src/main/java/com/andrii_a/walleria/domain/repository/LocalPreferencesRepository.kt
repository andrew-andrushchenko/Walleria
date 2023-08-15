package com.andrii_a.walleria.domain.repository

import kotlinx.coroutines.flow.Flow
import com.andrii_a.walleria.domain.CollectionListLayoutType
import com.andrii_a.walleria.domain.PhotoQuality
import com.andrii_a.walleria.domain.PhotosListLayoutType

interface LocalPreferencesRepository {

    val photosListLayoutType: Flow<PhotosListLayoutType>

    val collectionsListLayoutType: Flow<CollectionListLayoutType>

    val photosLoadQuality: Flow<PhotoQuality>

    val photosDownloadQuality: Flow<PhotoQuality>

    suspend fun updatePhotosListLayoutType(layoutType: PhotosListLayoutType)

    suspend fun updateCollectionsListLayoutType(layoutType: CollectionListLayoutType)

    suspend fun updatePhotosLoadQuality(photoQuality: PhotoQuality)

    suspend fun updatePhotosDownloadQuality(photoQuality: PhotoQuality)

}