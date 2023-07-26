package com.andrii_a.walleria.domain.models.login

import com.andrii_a.walleria.domain.models.photo.Photo
import com.andrii_a.walleria.domain.models.photo.PhotoLinks
import com.andrii_a.walleria.domain.models.user.UserProfileImage

data class MyProfile(
    val id: String,
    val updatedAt: String?,
    val firstName: String,
    val lastName: String,
    val twitterUsername: String?,
    val portfolioUrl: String?,
    val bio: String?,
    val location: String?,
    val links: PhotoLinks?,
    val profileImage: UserProfileImage?,
    val instagramUsername: String?,
    val totalLikes: Int,
    val totalPhotos: Int,
    val totalCollections: Int,
    val photos: List<Photo>?,
    val followedByUser: Boolean,
    val followersCount: Int,
    val followingCount: Int,
    val downloads: Int,
    val email: String?
)
