package com.andrii_a.walleria.domain.models.photo

import com.andrii_a.walleria.domain.models.collection.Collection

data class RelatedCollections(
    val results: List<Collection>?
)