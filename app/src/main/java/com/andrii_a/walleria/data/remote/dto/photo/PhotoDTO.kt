package com.andrii_a.walleria.data.remote.dto.photo

import com.andrii_a.walleria.data.remote.dto.collection.CollectionDTO
import com.andrii_a.walleria.data.remote.dto.common.TagDTO
import com.andrii_a.walleria.data.remote.dto.user.UserDTO
import com.andrii_a.walleria.domain.models.photo.Photo
import com.google.gson.annotations.SerializedName

data class PhotoDTO(
    val id: String,
    val width: Int,
    val height: Int,
    @SerializedName("blur_hash")
    val blurHash: String?,
    val color: String?,
    val views: Long?,
    val downloads: Long?,
    val likes: Long?,
    @SerializedName("liked_by_user")
    val likedByUser: Boolean,
    val description: String?,
    @SerializedName("alt_description")
    val altDescription: String?,
    val exif: PhotoExifDTO?,
    val location: PhotoLocationDTO?,
    val tags: List<TagDTO>?,
    @SerializedName("related_collections")
    val relatedCollections: RelatedCollectionsDTO?,
    @SerializedName("current_user_collections")
    val currentUserCollections: List<CollectionDTO>?,
    val sponsorship: PhotoSponsorshipDTO?,
    val urls: PhotoUrlsDTO,
    val links: PhotoLinksDTO?,
    val user: UserDTO?
) {

    fun toPhoto(): Photo = Photo(
        id = id,
        width = width,
        height = height,
        blurHash = blurHash,
        color = color ?: "#E0E0E0",
        views = views ?: 0,
        downloads = downloads ?: 0,
        likes = likes ?: 0,
        likedByUser = likedByUser,
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
