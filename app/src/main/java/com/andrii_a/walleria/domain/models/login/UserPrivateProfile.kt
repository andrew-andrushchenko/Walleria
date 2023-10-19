package com.andrii_a.walleria.domain.models.login

import com.andrii_a.walleria.domain.models.photo.Photo
import com.andrii_a.walleria.domain.models.photo.PhotoLinks
import com.andrii_a.walleria.domain.models.user.UserProfileImage

data class UserPrivateProfile(
    val id: String,
    val updatedAt: String?,
    val username: String,
    val firstName: String,
    val lastName: String,
    val twitterUsername: String?,
    val portfolioUrl: String?,
    val bio: String?,
    val location: String?,
    val links: PhotoLinks?,
    val profileImage: UserProfileImage?,
    val instagramUsername: String?,
    val totalLikes: Long,
    val totalPhotos: Long,
    val totalCollections: Long,
    val photos: List<Photo>?,
    val followedByUser: Boolean,
    val followersCount: Long,
    val followingCount: Long,
    val downloads: Long,
    val email: String?
)
