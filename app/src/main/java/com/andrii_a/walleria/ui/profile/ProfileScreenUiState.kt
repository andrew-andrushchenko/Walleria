package com.andrii_a.walleria.ui.profile

import com.andrii_a.walleria.domain.models.preferences.UserPrivateProfileData

data class ProfileScreenUiState(
    val isUserLoggedIn: Boolean = false,
    val shouldShowLogoutConfirmation: Boolean = false,
    val userPrivateProfileData: UserPrivateProfileData? = null,
)
