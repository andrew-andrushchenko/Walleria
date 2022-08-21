package com.andrii_a.walleria.data.remote.dto.topic

import com.andrii_a.walleria.data.remote.dto.photo.PhotoDTO
import com.andrii_a.walleria.domain.models.topic.Topic
import com.google.gson.annotations.SerializedName
import com.andrii_a.walleria.data.remote.dto.user.UserDTO

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
    val totalPhotos: Int?,
    val links: TopicLinksDTO?,
    val status: String?,
    val owners: List<UserDTO>?,
    @SerializedName("cover_photo")
    val coverPhoto: PhotoDTO?,
    @SerializedName("preview_photos")
    val previewPhotos: List<PhotoDTO>?
) {
    fun toTopic(): Topic = Topic(
        id = id,
        slug = slug,
        title = title,
        description = description,
        featured = featured,
        startsAt = startsAt,
        endsAt = endsAt,
        totalPhotos = totalPhotos,
        links = links?.toTopicLinks(),
        status = status,
        owners = owners?.map { it.toUser() },
        coverPhoto = coverPhoto?.toPhoto(),
        previewPhotos = previewPhotos?.map { it.toPhoto() }
    )
}