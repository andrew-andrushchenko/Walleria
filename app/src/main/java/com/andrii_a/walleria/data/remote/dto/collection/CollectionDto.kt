package com.andrii_a.walleria.data.remote.dto.collection

import com.andrii_a.walleria.data.remote.dto.common.TagDto
import com.andrii_a.walleria.data.remote.dto.photo.PhotoDto
import com.andrii_a.walleria.data.remote.dto.user.UserDto
import com.andrii_a.walleria.domain.models.collection.Collection
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CollectionDto(
    val id: String? = null,
    val title: String ? = null,
    val description: String? = null,
    @SerialName("published_at")
    val publishedAt: String? = null,
    @SerialName("updated_at")
    val updatedAt: String? = null,
    val curated: Boolean? = null,
    val featured: Boolean? = null,
    @SerialName("total_photos")
    val totalPhotos: Long? = null,
    val private: Boolean? = null,
    @SerialName("share_key")
    val shareKey: String? = null,
    val tags: List<TagDto>? = null,
    @SerialName("cover_photo")
    val coverPhoto: PhotoDto? = null,
    @SerialName("preview_photos")
    val previewPhotos: List<PhotoDto>? = null,
    val links: CollectionLinksDto? = null,
    val user: UserDto? = null
) {
    fun toCollection(): Collection = Collection(
        id = id.orEmpty(),
        title = title.orEmpty(),
        description = description,
        curated = curated ?: false,
        featured = featured ?: false,
        totalPhotos = totalPhotos ?: 0L,
        isPrivate = private ?: false,
        tags = tags?.map { it.toTag() },
        coverPhoto = coverPhoto?.toPhoto(),
        previewPhotos = previewPhotos?.map { it.toPhoto() },
        links = links?.toCollectionLinks(),
        user = user?.toUser()
    )
}