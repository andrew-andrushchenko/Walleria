package com.andrii_a.walleria.data.remote.dto.user

import kotlinx.serialization.Serializable

@Serializable
data class UserBadgeDto(
    val title: String? = null,
    val primary: Boolean? = null,
    val slug: String? = null,
    val link: String? = null
)