package com.andrii_a.walleria.ui.settings

import com.andrii_a.walleria.domain.AppTheme
import com.andrii_a.walleria.domain.PhotoQuality

sealed interface SettingsEvent {
    data class UpdateAppTheme(val theme: AppTheme) : SettingsEvent

    data class UpdatePhotosLoadQuality(val quality: PhotoQuality) : SettingsEvent

    data class UpdatePhotosDownloadQuality(val quality: PhotoQuality) : SettingsEvent

    data object GoBack : SettingsEvent
}