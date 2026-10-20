package com.andrii_a.walleria.data.repository.local

import com.andrii_a.walleria.domain.AppTheme
import com.andrii_a.walleria.domain.PhotoQuality
import com.andrii_a.walleria.domain.repository.LocalPreferencesRepository
import kotlinx.coroutines.flow.MutableStateFlow

class FakeLocalPreferencesRepository(
    initialAppTheme: AppTheme = AppTheme.SYSTEM_DEFAULT,
    initialLoadQuality: PhotoQuality = PhotoQuality.MEDIUM,
    initialDownloadQuality: PhotoQuality = PhotoQuality.HIGH
) : LocalPreferencesRepository {

    override val appTheme = MutableStateFlow(initialAppTheme)
    override val photosLoadQuality = MutableStateFlow(initialLoadQuality)
    override val photosDownloadQuality = MutableStateFlow(initialDownloadQuality)

    override suspend fun updateAppTheme(appTheme: AppTheme) {
        this.appTheme.value = appTheme
    }

    override suspend fun updatePhotosLoadQuality(photoQuality: PhotoQuality) {
        this.photosLoadQuality.value = photoQuality
    }

    override suspend fun updatePhotosDownloadQuality(photoQuality: PhotoQuality) {
        this.photosDownloadQuality.value = photoQuality
    }
}