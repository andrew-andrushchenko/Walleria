package com.andrii_a.walleria.domain.models.photo

data class PhotoLocation(
    val city: String?,
    val country: String?,
    val position: Position?
) {

    data class Position(
        val latitude: Double?,
        val longitude: Double?
    )
}