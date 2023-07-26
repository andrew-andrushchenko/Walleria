package com.andrii_a.walleria.domain.models.collection

import com.andrii_a.walleria.domain.models.common.Tag
import com.andrii_a.walleria.domain.models.photo.Photo
import com.andrii_a.walleria.domain.models.user.User

data class Collection(
    val id: String,
    val title: String,
    val description: String?,
    val curated: Boolean,
    val featured: Boolean,
    val totalPhotos: Long,
    val isPrivate: Boolean,
    val tags: List<Tag>?,
    val coverPhoto: Photo?,
    val previewPhotos: List<Photo>?,
    val links: CollectionLinks?,
    val user: User?
)