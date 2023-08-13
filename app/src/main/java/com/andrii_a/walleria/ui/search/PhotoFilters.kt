package com.andrii_a.walleria.ui.search

import com.andrii_a.walleria.domain.SearchResultsContentFilter
import com.andrii_a.walleria.domain.SearchResultsDisplayOrder
import com.andrii_a.walleria.domain.SearchResultsPhotoColor
import com.andrii_a.walleria.domain.SearchResultsPhotoOrientation

data class PhotoFilters(
    val order: SearchResultsDisplayOrder,
    val contentFilter: SearchResultsContentFilter,
    val color: SearchResultsPhotoColor,
    val orientation: SearchResultsPhotoOrientation
)