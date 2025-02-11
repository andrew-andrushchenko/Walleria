package com.andrii_a.walleria.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andrii_a.walleria.domain.repository.UserAccountPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userAccountPreferencesRepository: UserAccountPreferencesRepository
) : ViewModel() {

    private val _state: MutableStateFlow<ProfileScreenUiState> = MutableStateFlow(
        ProfileScreenUiState()
    )
    val state = combine(
        userAccountPreferencesRepository.isUserLoggedIn,
        userAccountPreferencesRepository.userPrivateProfileData,
        _state
    ) { isUserLoggedIn, userPrivateProfileData, state ->
        state.copy(
            isUserLoggedIn = isUserLoggedIn,
            userPrivateProfileData = userPrivateProfileData,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = _state.value
    )

    private val navigationChannel = Channel<ProfileScreenNavigationEvent>()
    val navigationEventsChannelFlow = navigationChannel.receiveAsFlow()

    fun onEvent(event: ProfileScreenEvent) {
        when (event) {
            is ProfileScreenEvent.Logout -> {
                logout()
            }

            is ProfileScreenEvent.ToggleLogoutConfirmation -> {
                _state.update {
                    it.copy(shouldShowLogoutConfirmation = event.isShown)
                }
            }

            is ProfileScreenEvent.OpenLoginScreen -> {
                viewModelScope.launch {
                    navigationChannel.send(ProfileScreenNavigationEvent.NavigateToLoginScreen)
                }
            }

            is ProfileScreenEvent.OpenViewProfileScreen -> {
                viewModelScope.launch {
                    navigationChannel.send(
                        ProfileScreenNavigationEvent.NavigateToViewProfileScreen(
                            event.nickname
                        )
                    )
                }
            }

            is ProfileScreenEvent.OpenEditProfileScreen -> {
                viewModelScope.launch {
                    navigationChannel.send(
                        ProfileScreenNavigationEvent.NavigateToEditProfileScreen
                    )
                }
            }

            is ProfileScreenEvent.OpenAboutScreen -> {
                viewModelScope.launch {
                    navigationChannel.send(ProfileScreenNavigationEvent.NavigateToAboutScreen)
                }
            }

            is ProfileScreenEvent.OpenSettingsScreen -> {
                viewModelScope.launch {
                    navigationChannel.send(ProfileScreenNavigationEvent.NavigateToSettingsScreen)
                }
            }

            is ProfileScreenEvent.GoBack -> {
                viewModelScope.launch {
                    navigationChannel.send(ProfileScreenNavigationEvent.NavigateBack)
                }
            }
        }
    }

    private fun logout() {
        viewModelScope.launch {
            withContext(NonCancellable) {
                userAccountPreferencesRepository.clearAccountInfo()
            }
        }
    }
}