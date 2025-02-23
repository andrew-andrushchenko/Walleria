package com.andrii_a.walleria.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andrii_a.walleria.domain.PhotoQuality
import com.andrii_a.walleria.domain.repository.LocalPreferencesRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

sealed interface SettingsEvent {
    data class UpdatePhotosLoadQuality(val quality: PhotoQuality) : SettingsEvent
    data class UpdatePhotosDownloadQuality(val quality: PhotoQuality) : SettingsEvent
}

class SettingsViewModel(private val repository: LocalPreferencesRepository) : ViewModel() {

    val photosLoadQuality: StateFlow<PhotoQuality> = repository.photosLoadQuality
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = runBlocking { repository.photosLoadQuality.first() }
        )

    val photosDownloadQuality: StateFlow<PhotoQuality> = repository.photosDownloadQuality
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = runBlocking { repository.photosLoadQuality.first() }
        )

    fun onEvent(event: SettingsEvent) {
        when (event) {
            is SettingsEvent.UpdatePhotosLoadQuality -> {
                viewModelScope.launch {
                    repository.updatePhotosLoadQuality(event.quality)
                }
            }

            is SettingsEvent.UpdatePhotosDownloadQuality -> {
                viewModelScope.launch {
                    repository.updatePhotosDownloadQuality(event.quality)
                }
            }
        }
    }
}