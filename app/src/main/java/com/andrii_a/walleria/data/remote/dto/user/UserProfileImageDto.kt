package com.andrii_a.walleria.data.remote.dto.user

import com.andrii_a.walleria.domain.models.user.UserProfileImage
import kotlinx.serialization.Serializable

@Serializable
data class UserProfileImageDto(
    val small: String? = null,
    val medium: String? = null,
    val large: String? = null
) {
    fun toUserProfileImage(): UserProfileImage = UserProfileImage(
        small = small.orEmpty(),
        medium = medium.orEmpty(),
        large = large.orEmpty()
    )
}