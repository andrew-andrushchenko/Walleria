package com.andrii_a.walleria.data.remote.dto.search

import com.google.gson.annotations.SerializedName
import com.andrii_a.walleria.data.remote.dto.photo.PhotoDTO

data class SearchPhotosResultDTO(
    val total: Int,
    @SerializedName("total_pages")
    val totalPages: Int,
    val results: List<PhotoDTO>
)