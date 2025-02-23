package com.andrii_a.walleria.data.remote.dto.common

import com.andrii_a.walleria.domain.models.common.Tag
import kotlinx.serialization.Serializable

@Serializable
data class TagDto(val title: String? = null) {
    fun toTag(): Tag = Tag(title = title.orEmpty())
}