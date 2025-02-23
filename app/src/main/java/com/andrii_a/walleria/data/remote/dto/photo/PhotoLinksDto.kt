package com.andrii_a.walleria.data.remote.dto.photo

import com.andrii_a.walleria.domain.models.photo.PhotoLinks
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PhotoLinksDto(
    val self: String? = null,
    val html: String? = null,
    val download: String? = null,
    @SerialName("download_location")
    val downloadLocation: String? = null
) {
    fun toPhotoLinks(): PhotoLinks = PhotoLinks(
        html = html.orEmpty(),
        download = download.orEmpty()
    )
}