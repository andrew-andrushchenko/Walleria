package com.andrii_a.walleria.ui.account

sealed interface AccountScreenEvent {
    data object Logout : AccountScreenEvent

    data class ToggleLogoutConfirmation(val isShown: Boolean) : AccountScreenEvent

    data object OpenLoginScreen : AccountScreenEvent

    data class OpenViewProfileScreen(val nickname: String) : AccountScreenEvent

    data object OpenEditProfileScreen : AccountScreenEvent

    data object OpenSettingsScreen : AccountScreenEvent

    data object OpenAboutScreen : AccountScreenEvent
}