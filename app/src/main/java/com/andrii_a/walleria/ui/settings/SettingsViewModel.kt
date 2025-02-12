package com.andrii_a.walleria.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andrii_a.walleria.domain.PhotoQuality
import com.andrii_a.walleria.domain.repository.LocalPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

sealed interface SettingsEvent {
    data class UpdatePhotosLoadQuality(val quality: PhotoQuality) : SettingsEvent
    data class UpdatePhotosDownloadQuality(val quality: PhotoQuality) : SettingsEvent
}

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val localPreferencesRepository: LocalPreferencesRepository
) : ViewModel() {

    val photosLoadQuality: StateFlow<PhotoQuality> = localPreferencesRepository.photosLoadQuality
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = runBlocking { localPreferencesRepository.photosLoadQuality.first() }
        )

    val photosDownloadQuality: StateFlow<PhotoQuality> = localPreferencesRepository.photosDownloadQuality
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = runBlocking { localPreferencesRepository.photosLoadQuality.first() }
        )

    fun onEvent(event: SettingsEvent) {
        when (event) {
            is SettingsEvent.UpdatePhotosLoadQuality -> {
                viewModelScope.launch {
                    localPreferencesRepository.updatePhotosLoadQuality(event.quality)
                }
            }

            is SettingsEvent.UpdatePhotosDownloadQuality -> {
                viewModelScope.launch {
                    localPreferencesRepository.updatePhotosDownloadQuality(event.quality)
                }
            }
        }
    }
}