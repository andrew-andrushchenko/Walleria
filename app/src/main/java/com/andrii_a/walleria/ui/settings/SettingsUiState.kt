package com.andrii_a.walleria.ui.settings

import com.andrii_a.walleria.domain.PhotoQuality

data class SettingsUiState(
    val photosLoadQuality: PhotoQuality = PhotoQuality.MEDIUM,
    val photosDownloadQuality: PhotoQuality = PhotoQuality.HIGH
)
