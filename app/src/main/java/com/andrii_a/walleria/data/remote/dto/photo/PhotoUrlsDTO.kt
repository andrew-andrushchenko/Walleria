package com.andrii_a.walleria.data.remote.dto.photo

import com.andrii_a.walleria.domain.models.photo.PhotoUrls

data class PhotoUrlsDTO(
    val raw: String,
    val full: String,
    val regular: String,
    val small: String,
    val thumb: String
) {
    fun toPhotoUrls(): PhotoUrls = PhotoUrls(
        raw = raw,
        full = full,
        regular = regular,
        small = small,
        thumb = thumb
    )
}