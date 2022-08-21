package com.andrii_a.walleria.data.remote.dto.user

import com.andrii_a.walleria.domain.models.user.UserBadge

data class UserBadgeDTO(
    val title: String?,
    val primary: Boolean?,
    val slug: String?,
    val link: String?
) {
    fun toUserBadge(): UserBadge = UserBadge(
        title = title,
        primary = primary,
        slug = slug,
        link = link
    )
}