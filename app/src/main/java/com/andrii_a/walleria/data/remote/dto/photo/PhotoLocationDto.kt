package com.andrii_a.walleria.data.remote.dto.photo

import com.andrii_a.walleria.domain.models.photo.PhotoLocation
import kotlinx.serialization.Serializable

@Serializable
data class PhotoLocationDto(
    val city: String? = null,
    val country: String? = null,
    val position: PositionDTO? = null
) {
    fun toLocation(): PhotoLocation = PhotoLocation(
        city = city,
        country = country,
        position = position?.toPosition()
    )

    @Serializable
    data class PositionDTO(
        val latitude: Double? = null,
        val longitude: Double? = null
    ) {
        fun toPosition(): PhotoLocation.Position = PhotoLocation.Position(
            latitude = latitude,
            longitude = longitude
        )
    }
}