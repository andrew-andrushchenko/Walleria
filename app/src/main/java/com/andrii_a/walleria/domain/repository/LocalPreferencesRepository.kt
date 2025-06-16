package com.andrii_a.walleria.domain.repository

import com.andrii_a.walleria.domain.AppTheme
import com.andrii_a.walleria.domain.PhotoQuality
import kotlinx.coroutines.flow.Flow

interface LocalPreferencesRepository {

    val appTheme: Flow<AppTheme>

    val photosLoadQuality: Flow<PhotoQuality>

    val photosDownloadQuality: Flow<PhotoQuality>

    suspend fun updateAppTheme(appTheme: AppTheme)

    suspend fun updatePhotosLoadQuality(photoQuality: PhotoQuality)

    suspend fun updatePhotosDownloadQuality(photoQuality: PhotoQuality)

}