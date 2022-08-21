package com.andrii_a.walleria.data.remote.dto.search

import com.andrii_a.walleria.data.remote.dto.collection.CollectionDTO
import com.google.gson.annotations.SerializedName

data class SearchCollectionsResultDTO(
    val total: Int,
    @SerializedName("total_pages")
    val totalPages: Int,
    val results: List<CollectionDTO>
)