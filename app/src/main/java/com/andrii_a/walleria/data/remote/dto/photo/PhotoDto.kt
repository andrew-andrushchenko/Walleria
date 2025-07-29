package com.andrii_a.walleria.data.remote.dto.photo

import com.andrii_a.walleria.data.remote.dto.collection.CollectionDto
import com.andrii_a.walleria.data.remote.dto.common.TagDto
import com.andrii_a.walleria.data.remote.dto.user.UserDto
import com.andrii_a.walleria.domain.models.photo.Photo
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PhotoDto(
    val id: String? = null,
    @SerialName("created_at")
    val createdAt: String? = null,
    @SerialName("updated_at")
    val updatedAt: String? = null,
    val width: Int? = null,
    val height: Int? = null,
    val color: String? = null,
    @SerialName("blur_hash")
    val blurHash: String? = null,
    val downloads: Long? = null,
    val likes: Long? = null,
    @SerialName("liked_by_user")
    val likedByUser: Boolean? = null,
    val views: Long? = null,
    val description: String? = null,
    @SerialName("alt_description")
    val altDescription: String? = null,
    val exif: PhotoExifDto? = null,
    val location: PhotoLocationDto? = null,
    val tags: List<TagDto>? = null,
    @SerialName("related_collections")
    val relatedCollections: RelatedCollectionsDto? = null,
    @SerialName("current_user_collections")
    val currentUserCollections: List<CollectionDto>? = null,
    val sponsorship: PhotoSponsorshipDto? = null,
    val urls: PhotoUrlsDto,
    val links: PhotoLinksDto? = null,
    val user: UserDto? = null
) {

    fun toPhoto(): Photo = Photo(
        id = id.orEmpty(),
        width = width ?: 0,
        height = height ?: 0,
        createdAt = createdAt.orEmpty(),
        blurHash = blurHash,
        color = color ?: "#E0E0E0",
        views = views ?: 0,
        downloads = downloads ?: 0,
        likes = likes ?: 0,
        likedByUser = likedByUser ?: false,
        description = description,
        exif = exif?.toExif(),
        location = location?.toLocation(),
        tags = tags?.map { it.toTag() },
        relatedCollections = relatedCollections?.toRelatedCollections(),
        currentUserCollections = currentUserCollections?.map { it.toCollection() },
        sponsorship = sponsorship?.toPhotoSponsorship(),
        urls = urls.toPhotoUrls(),
        links = links?.toPhotoLinks(),
        user = user?.toUser()
    )
}
