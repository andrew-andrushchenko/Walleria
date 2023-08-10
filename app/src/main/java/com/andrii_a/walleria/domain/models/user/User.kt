package com.andrii_a.walleria.domain.models.user

import com.andrii_a.walleria.domain.models.photo.Photo

data class User(
    val id: String,
    val username: String,
    val firstName: String,
    val lastName: String,
    val bio: String?,
    val location: String?,
    val totalLikes: Long,
    val totalPhotos: Long,
    val totalCollections: Long,
    val followersCount: Long,
    val followingCount: Long,
    val downloads: Long,
    val profileImage: UserProfileImage?,
    val social: UserSocialMediaLinks?,
    val tags: UserTags?,
    val photos: List<Photo>?
)