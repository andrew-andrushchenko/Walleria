package com.andrii_a.walleria.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andrii_a.walleria.domain.CollectionListLayoutType
import com.andrii_a.walleria.domain.PhotoQuality
import com.andrii_a.walleria.domain.PhotosListLayoutType
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
    data class UpdatePhotosListLayoutType(val layoutType: PhotosListLayoutType) : SettingsEvent
    data class UpdateCollectionsListLayoutType(val layoutType: CollectionListLayoutType) : SettingsEvent
    data class UpdatePhotosLoadQuality(val quality: PhotoQuality) : SettingsEvent
}

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val localPreferencesRepository: LocalPreferencesRepository
) : ViewModel() {

    val photosListLayoutType: StateFlow<PhotosListLayoutType> = localPreferencesRepository.photosListLayoutType
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = runBlocking { localPreferencesRepository.photosListLayoutType.first() }
        )

    val collectionsListLayoutType: StateFlow<CollectionListLayoutType> = localPreferencesRepository.collectionsListLayoutType
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = runBlocking { localPreferencesRepository.collectionsListLayoutType.first() }
        )

    val photosLoadQuality: StateFlow<PhotoQuality> = localPreferencesRepository.photosLoadQuality
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = runBlocking { localPreferencesRepository.photosLoadQuality.first() }
        )

    fun onEvent(event: SettingsEvent) {
        when (event) {
            is SettingsEvent.UpdatePhotosListLayoutType -> {
                viewModelScope.launch {
                    localPreferencesRepository.updatePhotosListLayoutType(event.layoutType)
                }
            }
            is SettingsEvent.UpdateCollectionsListLayoutType -> {
                viewModelScope.launch {
                    localPreferencesRepository.updateCollectionsListLayoutType(event.layoutType)
                }
            }
            is SettingsEvent.UpdatePhotosLoadQuality -> {
                viewModelScope.launch {
                    localPreferencesRepository.updatePhotosLoadQuality(event.quality)
                }
            }
        }
    }
}