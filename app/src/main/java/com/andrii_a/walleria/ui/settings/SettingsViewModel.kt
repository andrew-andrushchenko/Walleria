package com.andrii_a.walleria.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andrii_a.walleria.domain.repository.LocalPreferencesRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(private val repository: LocalPreferencesRepository) : ViewModel() {

    private val _state: MutableStateFlow<SettingsUiState> = MutableStateFlow(SettingsUiState())
    val state = combine(
        repository.appTheme,
        repository.photosLoadQuality,
        repository.photosDownloadQuality,
        _state
    ) { appTheme, photosLoadQuality, photosDownloadQuality, state ->
        state.copy(
            appTheme = appTheme,
            photosLoadQuality = photosLoadQuality,
            photosDownloadQuality = photosDownloadQuality
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = _state.value
    )

    private val navigationEventChannel = Channel<SettingsNavigationEvent>()
    val navigationEventFlow = navigationEventChannel.receiveAsFlow()

    fun onEvent(event: SettingsEvent) {
        when (event) {
            is SettingsEvent.UpdateAppTheme -> {
                viewModelScope.launch {
                    repository.updateAppTheme(event.theme)
                }
            }

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

            is SettingsEvent.GoBack -> {
                viewModelScope.launch {
                    navigationEventChannel.send(SettingsNavigationEvent.NavigateBack)
                }
            }
        }
    }
}