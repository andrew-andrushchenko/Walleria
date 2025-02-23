package com.andrii_a.walleria.data.remote.dto.topic

import com.andrii_a.walleria.data.remote.dto.photo.PhotoDto
import com.andrii_a.walleria.data.remote.dto.user.UserDto
import com.andrii_a.walleria.domain.TopicStatus
import com.andrii_a.walleria.domain.models.topic.Topic
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TopicDto(
    val id: String? = null,
    val slug: String? = null,
    val title: String? = null,
    val description: String? = null,
    val featured: Boolean? = null,
    @SerialName("published_at")
    val publishedAt: String? = null,
    @SerialName("updated_at")
    val updatedAt: String? = null,
    @SerialName("starts_at")
    val startsAt: String? = null,
    @SerialName("ends_at")
    val endsAt: String? = null,
    @SerialName("total_photos")
    val totalPhotos: Long? = null,
    val links: TopicLinksDto? = null,
    val status: String? = null,
    val owners: List<UserDto>? = null,
    @SerialName("cover_photo")
    val coverPhoto: PhotoDto? = null,
    @SerialName("preview_photos")
    val previewPhotos: List<PhotoDto>? = null
) {
    fun toTopic(): Topic {
        val domainTopicStatus = when (status?.lowercase()) {
            "open" -> TopicStatus.OPEN
            "closed" -> TopicStatus.CLOSED
            else -> TopicStatus.OTHER
        }

        return Topic(
            id = id.orEmpty(),
            title = title.orEmpty(),
            description = description,
            featured = featured ?: false,
            startsAt = startsAt.orEmpty(),
            endsAt = endsAt,
            updatedAt = updatedAt,
            totalPhotos = totalPhotos ?: 0,
            links = links?.toTopicLinks(),
            status = domainTopicStatus,
            owners = owners?.map { it.toUser() },
            coverPhoto = coverPhoto?.toPhoto(),
            previewPhotos = previewPhotos?.map { it.toPhoto() }
        )
    }
}