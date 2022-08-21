package com.andrii_a.walleria.data.remote.dto.collection

import com.andrii_a.walleria.domain.models.collection.CollectionLinks

data class CollectionLinksDTO(
    val self: String?,
    val html: String?,
    val photos: String?,
    val related: String?
) {
    fun toCollectionLinks(): CollectionLinks = CollectionLinks(
        self = self,
        html = html,
        photos = photos,
        related = related
    )
}