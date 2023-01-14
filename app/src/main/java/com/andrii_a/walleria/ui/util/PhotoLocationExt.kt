package com.andrii_a.walleria.ui.util

import com.andrii_a.walleria.domain.models.photo.PhotoLocation

val PhotoLocation.locationString: String?
    get() = when {
        city != null && country != null -> "$city, $country"
        city != null && country == null -> city
        city == null && country != null -> country
        else -> null
    }