package com.andrii_a.walleria.ui.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andrii_a.walleria.domain.repository.LocalAccountRepository
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

class AccountViewModel(private val repository: LocalAccountRepository) : ViewModel() {

    private val _state: MutableStateFlow<AccountScreenUiState> = MutableStateFlow(AccountScreenUiState())
    val state = combine(
        repository.isUserLoggedIn,
        repository.userPrivateProfileData,
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

    private val navigationChannel = Channel<AccountScreenNavigationEvent>()
    val navigationEventsChannelFlow = navigationChannel.receiveAsFlow()

    fun onEvent(event: AccountScreenEvent) {
        when (event) {
            is AccountScreenEvent.Logout -> {
                logout()
            }

            is AccountScreenEvent.ToggleLogoutConfirmation -> {
                _state.update {
                    it.copy(shouldShowLogoutConfirmation = event.isShown)
                }
            }

            is AccountScreenEvent.OpenLoginScreen -> {
                viewModelScope.launch {
                    navigationChannel.send(AccountScreenNavigationEvent.NavigateToLoginScreen)
                }
            }

            is AccountScreenEvent.OpenViewProfileScreen -> {
                viewModelScope.launch {
                    navigationChannel.send(
                        AccountScreenNavigationEvent.NavigateToViewAccountScreen(
                            event.nickname
                        )
                    )
                }
            }

            is AccountScreenEvent.OpenEditProfileScreen -> {
                viewModelScope.launch {
                    navigationChannel.send(
                        AccountScreenNavigationEvent.NavigateToEditAccountScreen
                    )
                }
            }

            is AccountScreenEvent.OpenAboutScreen -> {
                viewModelScope.launch {
                    navigationChannel.send(AccountScreenNavigationEvent.NavigateToAboutScreen)
                }
            }

            is AccountScreenEvent.OpenSettingsScreen -> {
                viewModelScope.launch {
                    navigationChannel.send(AccountScreenNavigationEvent.NavigateToSettingsScreen)
                }
            }
        }
    }

    private fun logout() {
        viewModelScope.launch {
            withContext(NonCancellable) {
                repository.clearAccountInfo()
            }
        }
    }
}