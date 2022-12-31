package com.andrii_a.walleria.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.andrii_a.walleria.R
import androidx.compose.ui.graphics.Color as ComposeColor
import android.graphics.Color as AndroidColor
import com.andrii_a.walleria.core.PhotoQuality
import com.andrii_a.walleria.core.UserProfileImageQuality
import com.andrii_a.walleria.domain.models.photo.Photo

fun Photo.getUrlByQuality(quality: PhotoQuality = PhotoQuality.HIGH): String =
    when (quality) {
        PhotoQuality.RAW -> this.urls.raw
        PhotoQuality.HIGH -> this.urls.full
        PhotoQuality.MEDIUM -> this.urls.regular
        PhotoQuality.LOW -> this.urls.small
        PhotoQuality.THUMBNAIL -> this.urls.thumb
    }

fun Photo.getUserProfileImageUrlOrEmpty(quality: UserProfileImageQuality = UserProfileImageQuality.MEDIUM): String =
    when (quality) {
        UserProfileImageQuality.LOW -> this.user?.profileImage?.small.orEmpty()
        UserProfileImageQuality.MEDIUM -> this.user?.profileImage?.medium.orEmpty()
        UserProfileImageQuality.HIGH -> this.user?.profileImage?.large.orEmpty()
    }

val Photo.userFullName: String
    @Composable
    get() = stringResource(
        id = R.string.user_full_name_formatted,
        this.user?.firstName.orEmpty(),
        this.user?.lastName.orEmpty()
    )

val Photo.userNickname: String
    get() = this.user?.username.orEmpty()

val Photo.primaryColorInt: Int
    get() = this.color?.let { AndroidColor.parseColor(it) } ?: AndroidColor.GRAY

val Photo.primaryColorComposable: ComposeColor
    get() = ComposeColor(this.primaryColorInt)