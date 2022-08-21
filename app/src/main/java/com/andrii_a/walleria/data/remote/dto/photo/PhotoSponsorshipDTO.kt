package com.andrii_a.walleria.data.remote.dto.photo

import com.andrii_a.walleria.domain.models.photo.PhotoSponsorship
import com.andrii_a.walleria.data.remote.dto.user.UserDTO

data class PhotoSponsorshipDTO(val sponsor: UserDTO?) {
    fun toPhotoSponsorship(): PhotoSponsorship = PhotoSponsorship(
        sponsor = sponsor?.toUser()
    )
}