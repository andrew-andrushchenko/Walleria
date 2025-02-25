package com.andrii_a.walleria.ui.util

import com.andrii_a.walleria.domain.PhotoQuality
import com.andrii_a.walleria.domain.models.collection.Collection

val Collection.username: String
    get() = this.user?.username.orEmpty()

fun Collection?.getCoverPhotoUrl(
    quality: PhotoQuality = PhotoQuality.MEDIUM
) = this?.coverPhoto?.getUrlByQuality(quality).orEmpty()