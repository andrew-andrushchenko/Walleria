package com.andrii_a.walleria.data.remote.dto.photo

import com.andrii_a.walleria.data.remote.dto.collection.CollectionDTO
import com.andrii_a.walleria.domain.models.photo.RelatedCollections

data class RelatedCollectionsDTO(
    val total: Int,
    val results: List<CollectionDTO>?
) {
    fun toRelatedCollections(): RelatedCollections = RelatedCollections(
        results = results?.map { it.toCollection() }
    )
}