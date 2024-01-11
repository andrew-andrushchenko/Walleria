package com.andrii_a.walleria.ui.profile_edit

sealed interface EditUserProfileEvent {
    data class NicknameChanged(val value: String) : EditUserProfileEvent
    data class FirstNameChanged(val value: String) : EditUserProfileEvent
    data class LastNameChanged(val value: String) : EditUserProfileEvent
    data class EmailChanged(val value: String) : EditUserProfileEvent
    data class PortfolioLinkChanged(val value: String) : EditUserProfileEvent
    data class InstagramUsernameChanged(val value: String) : EditUserProfileEvent
    data class LocationChanged(val value: String) : EditUserProfileEvent
    data class BioChanged(val value: String) : EditUserProfileEvent
    data object SaveProfile : EditUserProfileEvent
    data object GoBack : EditUserProfileEvent
}