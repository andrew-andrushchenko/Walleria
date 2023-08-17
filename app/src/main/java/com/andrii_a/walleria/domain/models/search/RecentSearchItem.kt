package com.andrii_a.walleria.domain.models.search

data class RecentSearchItem(
    val id: Int = 0,
    val title: String,
    val timeMillis: Long
)
