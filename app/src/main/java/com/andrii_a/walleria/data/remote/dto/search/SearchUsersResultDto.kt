package com.andrii_a.walleria.data.remote.dto.search

import com.andrii_a.walleria.data.remote.dto.user.UserDto
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SearchUsersResultDto(
    val total: Int? = null,
    @SerialName("total_pages")
    val totalPages: Int? = null,
    val results: List<UserDto>? = null
)