package com.andrii_a.walleria.data.remote.dto.user

import com.andrii_a.walleria.domain.models.user.UserProfileImage

data class UserProfileImageDTO(
    val small: String,
    val medium: String,
    val large: String
) {
    fun toUserProfileImage(): UserProfileImage = UserProfileImage(
        small = small,
        medium = medium,
        large = large
    )
}