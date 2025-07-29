package com.andrii_a.walleria.ui.util

import android.text.format.DateFormat
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.core.graphics.toColorInt
import com.andrii_a.walleria.R
import com.andrii_a.walleria.domain.PhotoQuality
import com.andrii_a.walleria.domain.models.photo.Photo
import java.text.SimpleDateFormat
import java.util.Locale

fun Photo.getUrlByQuality(quality: PhotoQuality = PhotoQuality.HIGH): String =
    when (quality) {
        PhotoQuality.RAW -> this.urls.raw
        PhotoQuality.HIGH -> this.urls.full
        PhotoQuality.MEDIUM -> this.urls.regular
        PhotoQuality.LOW -> this.urls.small
        PhotoQuality.THUMBNAIL -> this.urls.thumb
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
    get() = this.color.toColorInt()

val Photo.downloadFilename: String
    get() = "${this.id}_${this.userNickname}_unsplash.jpg"

val Photo.createdDateTime: String
    get() {
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ROOT)
        val date = format.parse(this.createdAt)

        return DateFormat.format("E, MMM dd, yyyy \u2022 HH:mm", date).toString()
    }