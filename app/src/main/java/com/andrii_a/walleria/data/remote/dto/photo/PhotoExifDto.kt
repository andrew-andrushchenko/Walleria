package com.andrii_a.walleria.data.remote.dto.photo

import com.andrii_a.walleria.domain.models.photo.PhotoExif
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PhotoExifDto(
    val make: String? = null,
    val model: String? = null,
    @SerialName("exposure_time")
    val exposureTime: String? = null,
    val aperture: String? = null,
    @SerialName("focal_length")
    val focalLength: String? = null,
    val iso: Int? = null
) {
    fun toExif(): PhotoExif = PhotoExif(
        make = make,
        model = model,
        exposureTime = exposureTime,
        aperture = aperture,
        focalLength = focalLength,
        iso = iso
    )
}