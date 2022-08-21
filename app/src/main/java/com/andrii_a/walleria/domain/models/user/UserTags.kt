package com.andrii_a.walleria.domain.models.user

import com.andrii_a.walleria.domain.models.common.Tag

data class UserTags(
    val custom: List<Tag>?
)