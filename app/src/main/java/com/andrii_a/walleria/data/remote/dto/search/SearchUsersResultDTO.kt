package com.andrii_a.walleria.data.remote.dto.search

import com.google.gson.annotations.SerializedName
import com.andrii_a.walleria.data.remote.dto.user.UserDTO

data class SearchUsersResultDTO(
    val total: Int,
    @SerializedName("total_pages")
    val totalPages: Int,
    val results: List<UserDTO>
)