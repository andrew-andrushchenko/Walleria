package com.andrii_a.walleria.ui.profile_edit

sealed interface EditUserProfileNavigationEvent {
    data object NavigateBack : EditUserProfileNavigationEvent
}