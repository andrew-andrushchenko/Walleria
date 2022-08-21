package com.andrii_a.walleria.data.remote.dto.common

import com.andrii_a.walleria.domain.models.common.Tag

data class TagDTO(val title: String?) {
    fun toTag(): Tag = Tag(title = title)
}