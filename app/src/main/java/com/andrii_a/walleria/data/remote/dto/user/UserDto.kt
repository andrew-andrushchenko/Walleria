package com.andrii_a.walleria.data.remote.dto.user

import com.andrii_a.walleria.data.remote.dto.photo.PhotoDto
import com.andrii_a.walleria.domain.models.user.User
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    val id: String? = null,
    @SerialName("updated_at")
    val updatedAt: String? = null,
    val username: String? = null,
    val name: String? = null,
    @SerialName("first_name")
    val firstName: String? = null,
    @SerialName("last_name")
    val lastName: String? = null,
    val bio: String? = null,
    val location: String? = null,
    @SerialName("total_likes")
    val totalLikes: Long? = null,
    @SerialName("total_photos")
    val totalPhotos: Long? = null,
    @SerialName("total_collections")
    val totalCollections: Long? = null,
    @SerialName("followed_by_user")
    val followedByUser: Boolean? = null,
    @SerialName("followers_count")
    val followersCount: Long? = null,
    @SerialName("following_count")
    val followingCount: Long? = null,
    val downloads: Long? = null,
    @SerialName("profile_image")
    val profileImage: UserProfileImageDto? = null,
    val social: UserSocialMediaLinksDto? = null,
    val badge: UserBadgeDto? = null,
    val tags: UserTagsDto? = null,
    val photos: List<PhotoDto>? = null
) {
    fun toUser(): User = User(
        id = id.orEmpty(),
        username = username.orEmpty(),
        firstName = firstName.orEmpty(),
        lastName = lastName.orEmpty(),
        bio = bio,
        location = location,
        totalLikes = totalLikes ?: 0,
        totalPhotos = totalPhotos ?: 0,
        totalCollections = totalCollections ?: 0,
        followersCount = followersCount ?: 0,
        followingCount = followingCount ?: 0,
        downloads = downloads ?: 0,
        profileImage = profileImage?.toUserProfileImage(),
        social = social?.toUserSocial(),
        tags = tags?.toUserTags(),
        photos = photos?.map { it.toPhoto() }
    )
}