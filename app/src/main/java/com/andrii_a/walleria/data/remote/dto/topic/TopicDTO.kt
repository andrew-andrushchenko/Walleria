package com.andrii_a.walleria.data.remote.dto.topic

import com.andrii_a.walleria.core.TopicStatus
import com.andrii_a.walleria.data.remote.dto.photo.PhotoDTO
import com.andrii_a.walleria.data.remote.dto.user.UserDTO
import com.andrii_a.walleria.domain.models.topic.Topic
import com.google.gson.annotations.SerializedName

data class TopicDTO(
    val id: String,
    val slug: String?,
    val title: String?,
    val description: String?,
    val featured: Boolean?,
    @SerializedName("published_at")
    val publishedAt: String?,
    @SerializedName("updated_at")
    val updatedAt: String?,
    @SerializedName("starts_at")
    val startsAt: String?,
    @SerializedName("ends_at")
    val endsAt: String?,
    @SerializedName("total_photos")
    val totalPhotos: Long?,
    val links: TopicLinksDTO?,
    val status: String?,
    val owners: List<UserDTO>?,
    @SerializedName("cover_photo")
    val coverPhoto: PhotoDTO?,
    @SerializedName("preview_photos")
    val previewPhotos: List<PhotoDTO>?
) {
    fun toTopic(): Topic {
        val domainTopicStatus = when (status?.lowercase()) {
            "open" -> TopicStatus.OPEN
            "closed" -> TopicStatus.CLOSED
            else -> TopicStatus.OTHER
        }

        return Topic(
            id = id,
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