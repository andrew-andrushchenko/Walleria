package com.andrii_a.walleria.ui.account

import com.andrii_a.walleria.ui.common.UserNickname

sealed interface AccountScreenNavigationEvent {

    data class NavigateToViewAccountScreen(val nickname: UserNickname) : AccountScreenNavigationEvent

    data object NavigateToEditAccountScreen : AccountScreenNavigationEvent

    data object NavigateToLoginScreen : AccountScreenNavigationEvent

    data object NavigateToSettingsScreen : AccountScreenNavigationEvent

    data object NavigateToAboutScreen : AccountScreenNavigationEvent
}