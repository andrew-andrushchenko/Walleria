package com.andrii_a.walleria.ui.util

import com.andrii_a.walleria.core.PhotoQuality
import com.andrii_a.walleria.domain.models.collection.Collection

val Collection.username: String
    get() = this.user?.username.orEmpty()

val Collection.userFullName: String
    get() = "${this.user?.firstName.orEmpty()} ${this.user?.lastName.orEmpty()}"

fun Collection.getPreviewPhotos(
    maxPhotos: Int = 3,
    quality: PhotoQuality = PhotoQuality.MEDIUM
) = this.previewPhotos?.take(maxPhotos) ?: emptyList()

fun Collection?.getCoverPhotoUrl(
    quality: PhotoQuality = PhotoQuality.MEDIUM
) = this?.coverPhoto?.getUrlByQuality(quality).orEmpty()