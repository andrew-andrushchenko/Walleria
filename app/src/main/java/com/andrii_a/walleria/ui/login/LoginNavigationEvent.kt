package com.andrii_a.walleria.ui.login

sealed interface LoginNavigationEvent {

    data object NavigateToLoginCustomTab : LoginNavigationEvent

    data object NavigateToJoinCustomTab : LoginNavigationEvent

    data object NavigateBack : LoginNavigationEvent

}