package com.andrii_a.walleria.domain.repository

import com.andrii_a.walleria.domain.PhotoQuality
import kotlinx.coroutines.flow.Flow

interface LocalPreferencesRepository {

    val photosLoadQuality: Flow<PhotoQuality>

    val photosDownloadQuality: Flow<PhotoQuality>

    suspend fun updatePhotosLoadQuality(photoQuality: PhotoQuality)

    suspend fun updatePhotosDownloadQuality(photoQuality: PhotoQuality)

}