package com.andrii_a.walleria.domain.models.user

import com.andrii_a.walleria.domain.models.photo.Photo

data class User(
    val id: String,
    val username: String?,
    val name: String?,
    val firstName: String?,
    val lastName: String?,
    val bio: String?,
    val location: String?,
    val totalLikes: Int?,
    val totalPhotos: Int?,
    val totalCollections: Int?,
    val followersCount: Int?,
    val followingCount: Int?,
    val downloads: Int?,
    val profileImage: UserProfileImage?,
    val social: UserSocialMediaLinks?,
    val badge: UserBadge?,
    val tags: UserTags?,
    val photos: List<Photo>?
)