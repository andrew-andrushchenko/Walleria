package com.andrii_a.walleria.ui.search

import com.andrii_a.walleria.domain.SearchResultsContentFilter
import com.andrii_a.walleria.domain.SearchResultsDisplayOrder
import com.andrii_a.walleria.domain.SearchResultsPhotoColor
import com.andrii_a.walleria.domain.SearchResultsPhotoOrientation

data class PhotoFilters(
    val order: SearchResultsDisplayOrder = SearchResultsDisplayOrder.RELEVANT,
    val contentFilter: SearchResultsContentFilter = SearchResultsContentFilter.LOW,
    val color: SearchResultsPhotoColor = SearchResultsPhotoColor.ANY,
    val orientation: SearchResultsPhotoOrientation = SearchResultsPhotoOrientation.ANY
)