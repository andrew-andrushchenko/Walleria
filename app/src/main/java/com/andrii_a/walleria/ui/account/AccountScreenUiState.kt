package com.andrii_a.walleria.ui.account

import com.andrii_a.walleria.domain.models.preferences.UserPrivateProfileData

data class AccountScreenUiState(
    val isUserLoggedIn: Boolean = false,
    val shouldShowLogoutConfirmation: Boolean = false,
    val userPrivateProfileData: UserPrivateProfileData? = null,
)
