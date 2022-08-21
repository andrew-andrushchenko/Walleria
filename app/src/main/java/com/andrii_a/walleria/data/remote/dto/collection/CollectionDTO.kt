package com.andrii_a.walleria.data.remote.dto.collection

import com.google.gson.annotations.SerializedName
import com.andrii_a.walleria.data.remote.dto.common.TagDTO
import com.andrii_a.walleria.data.remote.dto.photo.PhotoDTO
import com.andrii_a.walleria.data.remote.dto.user.UserDTO
import com.andrii_a.walleria.domain.models.collection.Collection

data class CollectionDTO(
    val id: String,
    val title: String,
    val description: String?,
    @SerializedName("published_at")
    val publishedAt: String?,
    @SerializedName("updated_at")
    val updatedAt: String?,
    val curated: Boolean?,
    val featured: Boolean?,
    @SerializedName("total_photos")
    val totalPhotos: Int,
    val private: Boolean?,
    @SerializedName("share_key")
    val shareKey: String?,
    val tags: List<TagDTO>?,
    @SerializedName("cover_photo")
    val coverPhoto: PhotoDTO?,
    @SerializedName("preview_photos")
    val previewPhotos: List<PhotoDTO>?,
    val links: CollectionLinksDTO?,
    val user: UserDTO?
) {
    fun toCollection(): Collection = Collection(
        id = id,
        title = title,
        description = description,
        curated = curated,
        featured = featured,
        totalPhotos = totalPhotos,
        private = private,
        tags = tags?.map { it.toTag() },
        coverPhoto = coverPhoto?.toPhoto(),
        previewPhotos = previewPhotos?.map { it.toPhoto() },
        links = links?.toCollectionLinks(),
        user = user?.toUser()
    )
}