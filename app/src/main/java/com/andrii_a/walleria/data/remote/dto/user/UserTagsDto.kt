package com.andrii_a.walleria.data.remote.dto.user

import com.andrii_a.walleria.data.remote.dto.common.TagDto
import com.andrii_a.walleria.domain.models.user.UserTags
import kotlinx.serialization.Serializable

@Serializable
data class UserTagsDto(
    val custom: List<TagDto>? = null
) {
    fun toUserTags(): UserTags = UserTags(
        custom = custom?.map { it.toTag() }
    )
}