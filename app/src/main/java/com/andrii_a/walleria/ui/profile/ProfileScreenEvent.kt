package com.andrii_a.walleria.ui.profile

sealed interface ProfileScreenEvent {
    data object Logout : ProfileScreenEvent

    data class ToggleLogoutConfirmation(val isShown: Boolean) : ProfileScreenEvent

    data object OpenLoginScreen : ProfileScreenEvent

    data class OpenViewProfileScreen(val nickname: String) : ProfileScreenEvent

    data object OpenEditProfileScreen : ProfileScreenEvent

    data object OpenSettingsScreen : ProfileScreenEvent

    data object OpenAboutScreen : ProfileScreenEvent

    data object GoBack : ProfileScreenEvent
}