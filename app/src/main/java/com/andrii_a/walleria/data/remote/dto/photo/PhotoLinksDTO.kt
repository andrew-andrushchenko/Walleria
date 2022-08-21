package com.andrii_a.walleria.data.remote.dto.photo

import com.andrii_a.walleria.domain.models.photo.PhotoLinks
import com.google.gson.annotations.SerializedName

data class PhotoLinksDTO(
    val self: String,
    val html: String,
    val download: String?,
    @SerializedName("download_location")
    val downloadLocation: String
) {
    fun toPhotoLinks(): PhotoLinks = PhotoLinks(
        html = html,
        download = download
    )
}