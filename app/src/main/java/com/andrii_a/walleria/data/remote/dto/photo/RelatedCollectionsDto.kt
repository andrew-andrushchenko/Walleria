package com.andrii_a.walleria.data.remote.dto.photo

import com.andrii_a.walleria.data.remote.dto.collection.CollectionDto
import com.andrii_a.walleria.domain.models.photo.RelatedCollections
import kotlinx.serialization.Serializable

@Serializable
data class RelatedCollectionsDto(
    val total: Int? = null,
    val results: List<CollectionDto>? = null
) {
    fun toRelatedCollections(): RelatedCollections = RelatedCollections(
        results = results?.map { it.toCollection() }
    )
}