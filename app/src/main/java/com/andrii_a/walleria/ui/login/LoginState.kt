package com.andrii_a.walleria.ui.login

import com.andrii_a.walleria.domain.models.login.AccessToken

sealed interface LoginState {
    data object Empty : LoginState
    data object Loading : LoginState
    data object Error : LoginState
    data class Success(val accessToken: AccessToken) : LoginState
}