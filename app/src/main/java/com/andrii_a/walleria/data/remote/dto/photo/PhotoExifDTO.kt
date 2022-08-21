package com.andrii_a.walleria.data.remote.dto.photo

import com.andrii_a.walleria.domain.models.photo.PhotoExif
import com.google.gson.annotations.SerializedName

data class PhotoExifDTO(
    val make: String?,
    val model: String?,
    @SerializedName("exposure_time")
    val exposureTime: String?,
    val aperture: String?,
    @SerializedName("focal_length")
    val focalLength: String?,
    val iso: Int?
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