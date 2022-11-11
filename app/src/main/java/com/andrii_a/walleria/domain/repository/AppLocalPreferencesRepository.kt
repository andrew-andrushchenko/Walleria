package com.andrii_a.walleria.domain.repository

import kotlinx.coroutines.flow.Flow
import com.andrii_a.walleria.core.CollectionListLayoutType
import com.andrii_a.walleria.core.PhotoQuality
import com.andrii_a.walleria.core.PhotosListLayoutType

interface AppLocalPreferencesRepository {

    val photosListLayoutType: Flow<PhotosListLayoutType>

    val collectionsListLayoutType: Flow<CollectionListLayoutType>

    val imagePreviewsQuality: Flow<PhotoQuality>

    suspend fun savePhotosListLayoutType(photosListLayoutType: PhotosListLayoutType)

    suspend fun saveCollectionsListLayoutType(collectionListLayoutType: CollectionListLayoutType)

    suspend fun saveImagePreviewsQuality(imagePreviewsQuality: PhotoQuality)

}