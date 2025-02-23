package com.andrii_a.walleria.data.remote.dto.photo

import com.andrii_a.walleria.domain.models.photo.PhotoUrls
import kotlinx.serialization.Serializable

@Serializable
data class PhotoUrlsDto(
    val raw: String? = null,
    val full: String? = null,
    val regular: String? = null,
    val small: String? = null,
    val thumb: String? = null
) {
    fun toPhotoUrls(): PhotoUrls = PhotoUrls(
        raw = raw.orEmpty(),
        full = full.orEmpty(),
        regular = regular.orEmpty(),
        small = small.orEmpty(),
        thumb = thumb.orEmpty()
    )
}