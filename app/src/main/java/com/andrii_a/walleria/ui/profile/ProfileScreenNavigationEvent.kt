package com.andrii_a.walleria.ui.profile

import com.andrii_a.walleria.ui.common.UserNickname

sealed interface ProfileScreenNavigationEvent {

    data class NavigateToViewProfileScreen(val nickname: UserNickname) : ProfileScreenNavigationEvent

    data object NavigateToEditProfileScreen : ProfileScreenNavigationEvent

    data object NavigateToLoginScreen : ProfileScreenNavigationEvent

    data object NavigateToSettingsScreen : ProfileScreenNavigationEvent

    data object NavigateToAboutScreen : ProfileScreenNavigationEvent

    data object NavigateBack : ProfileScreenNavigationEvent
}