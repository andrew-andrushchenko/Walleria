package com.andrii_a.walleria.data.remote.dto.collection

import com.andrii_a.walleria.domain.models.collection.CollectionLinks
import kotlinx.serialization.Serializable

@Serializable
data class CollectionLinksDto(
    val self: String? = null,
    val html: String? = null,
    val photos: String? = null,
    val related: String? = null
) {
    fun toCollectionLinks(): CollectionLinks = CollectionLinks(
        self = self.orEmpty(),
        html = html.orEmpty(),
        photos = photos.orEmpty(),
        related = related.orEmpty()
    )
}