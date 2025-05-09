package com.andrii_a.walleria.ui.settings

sealed interface SettingsNavigationEvent {

    data object NavigateBack : SettingsNavigationEvent

}