package com.andrii_a.walleria.data.remote.dto.photo

import com.andrii_a.walleria.data.remote.dto.user.UserDto
import com.andrii_a.walleria.domain.models.photo.PhotoSponsorship
import kotlinx.serialization.Serializable

@Serializable
data class PhotoSponsorshipDto(val sponsor: UserDto? = null) {
    fun toPhotoSponsorship(): PhotoSponsorship = PhotoSponsorship(
        sponsor = sponsor?.toUser()
    )
}