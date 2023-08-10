package com.andrii_a.walleria.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.andrii_a.walleria.R
import com.andrii_a.walleria.domain.UserProfileImageQuality
import com.andrii_a.walleria.domain.models.photo.Photo
import com.andrii_a.walleria.domain.models.user.User

val User.userFullName: String
    @Composable
    get() = stringResource(
        id = R.string.user_full_name_formatted,
        this.firstName,
        this.lastName
    )

fun User.getProfileImageUrlOrEmpty(quality: UserProfileImageQuality = UserProfileImageQuality.MEDIUM): String =
    when (quality) {
        UserProfileImageQuality.LOW -> this.profileImage?.small.orEmpty()
        UserProfileImageQuality.MEDIUM -> this.profileImage?.medium.orEmpty()
        UserProfileImageQuality.HIGH -> this.profileImage?.large.orEmpty()
    }

fun User.getPreviewPhotos(maxPhotos: Int = 3): List<Photo> =
    this.photos?.takeIf { it.size > maxPhotos - 1 }?.take(maxPhotos) ?: emptyList()