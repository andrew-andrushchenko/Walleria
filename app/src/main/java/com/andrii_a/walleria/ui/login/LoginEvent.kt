package com.andrii_a.walleria.ui.login

sealed interface LoginEvent {

    data object PerformLogin : LoginEvent

    data object PerformJoin : LoginEvent

    data class GetAccessToken(val code: String) : LoginEvent

    data object PerformSaveUserProfile : LoginEvent

    data object DismissError : LoginEvent

    data object GoBack : LoginEvent

}