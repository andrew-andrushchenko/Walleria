package com.andrii_a.walleria.ui.profile_edit

data class EditUserProfileUiState(
    val nickname: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val portfolioLink: String = "",
    val instagramUsername: String = "",
    val location: String = "",
    val bio: String = "",
    val isNicknameValid: Boolean = true,
    val isEmailValid: Boolean = true
) {
    val isInputValid: Boolean
        get() = isNicknameValid && isEmailValid
}