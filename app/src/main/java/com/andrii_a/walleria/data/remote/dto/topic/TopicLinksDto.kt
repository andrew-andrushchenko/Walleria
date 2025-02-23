package com.andrii_a.walleria.data.remote.dto.topic

import com.andrii_a.walleria.domain.models.topic.TopicLinks
import kotlinx.serialization.Serializable

@Serializable
data class TopicLinksDto(
    val self: String? = null,
    val html: String? = null,
    val photos: String? = null
) {
    fun toTopicLinks(): TopicLinks = TopicLinks(
        self = self.orEmpty(),
        html = html.orEmpty(),
        photos = photos.orEmpty()
    )
}