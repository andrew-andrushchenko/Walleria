package com.andrii_a.walleria.ui.topic_details

import com.andrii_a.walleria.domain.PhotoListDisplayOrder
import com.andrii_a.walleria.domain.TopicPhotosOrientation

data class TopicPhotosFilters(
    val order: PhotoListDisplayOrder = PhotoListDisplayOrder.LATEST,
    val orientation: TopicPhotosOrientation = TopicPhotosOrientation.LANDSCAPE
)
