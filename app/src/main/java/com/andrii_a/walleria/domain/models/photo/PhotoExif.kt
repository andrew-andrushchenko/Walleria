package com.andrii_a.walleria.domain.models.photo

data class PhotoExif(
    val make: String?,
    val model: String?,
    val exposureTime: String?,
    val aperture: String?,
    val focalLength: String?,
    val iso: Int?
)