package com.andrii_a.walleria.domain.models.photo

import com.andrii_a.walleria.domain.models.collection.Collection
import com.andrii_a.walleria.domain.models.common.Tag
import com.andrii_a.walleria.domain.models.user.User

data class Photo(
    val id: String,
    val width: Int,
    val height: Int,
    val createdAt: String,
    val blurHash: String?,
    val color: String,
    val views: Long,
    val downloads: Long,
    val likes: Long,
    val likedByUser: Boolean,
    val description: String?,
    val exif: PhotoExif?,
    val location: PhotoLocation?,
    val tags: List<Tag>?,
    val relatedCollections: RelatedCollections?,
    val currentUserCollections: List<Collection>?,
    val sponsorship: PhotoSponsorship?,
    val urls: PhotoUrls,
    val links: PhotoLinks?,
    val user: User?
)