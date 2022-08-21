package com.andrii_a.walleria.data.remote.dto.user

import com.andrii_a.walleria.data.remote.dto.common.TagDTO
import com.andrii_a.walleria.domain.models.user.UserTags

data class UserTagsDTO(
    val custom: List<TagDTO>?
) {
    fun toUserTags(): UserTags = UserTags(
        custom = custom?.map { it.toTag() }
    )
}