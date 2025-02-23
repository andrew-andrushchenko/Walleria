package com.andrii_a.walleria.ui.login

import com.andrii_a.walleria.ui.common.UiError

data class LoginUiState(
    val isTokenObtained: Boolean = false,
    val isUserDataSaved: Boolean = false,
    val error: UiError? = null,
    val isLoading: Boolean = false,
) {
    val isLoggedIn = isTokenObtained && isUserDataSaved
}
