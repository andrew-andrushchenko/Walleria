package com.andrii_a.walleria.domain.models.preferences

data class UserPrivateProfileData(
    val nickname: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val profilePhotoUrl: String = "",
    val email: String = "",
    val portfolioLink: String = "",
    val instagramUsername: String = "",
    val location: String = "",
    val bio: String = ""
)