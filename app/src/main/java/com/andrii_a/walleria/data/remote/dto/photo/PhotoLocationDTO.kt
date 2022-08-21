package com.andrii_a.walleria.data.remote.dto.photo

import com.andrii_a.walleria.domain.models.photo.PhotoLocation

data class PhotoLocationDTO(
    val city: String?,
    val country: String?,
    val position: PositionDTO?
) {
    fun toLocation(): PhotoLocation = PhotoLocation(
        city = city,
        country = country,
        position = position?.toPosition()
    )

    data class PositionDTO(
        val latitude: Double?,
        val longitude: Double?
    ) {
        fun toPosition(): PhotoLocation.Position = PhotoLocation.Position(
            latitude = latitude,
            longitude = longitude
        )
    }
}