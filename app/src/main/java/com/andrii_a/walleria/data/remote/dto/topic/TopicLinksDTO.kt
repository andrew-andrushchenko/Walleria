package com.andrii_a.walleria.data.remote.dto.topic

import com.andrii_a.walleria.domain.models.topic.TopicLinks

data class TopicLinksDTO(
    val self: String?,
    val html: String?,
    val photos: String?
) {
    fun toTopicLinks(): TopicLinks = TopicLinks(
        self = self.orEmpty(),
        html = html.orEmpty(),
        photos = photos.orEmpty()
    )
}