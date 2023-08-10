package com.andrii_a.walleria.data.remote.dto.user

import com.andrii_a.walleria.data.remote.dto.photo.PhotoDTO
import com.andrii_a.walleria.domain.models.user.User
import com.google.gson.annotations.SerializedName

data class UserDTO(
    val id: String,
    @SerializedName("updated_at")
    val updatedAt: String?,
    val username: String?,
    val name: String?,
    @SerializedName("first_name")
    val firstName: String?,
    @SerializedName("last_name")
    val lastName: String?,
    val bio: String?,
    val location: String?,
    @SerializedName("total_likes")
    val totalLikes: Long?,
    @SerializedName("total_photos")
    val totalPhotos: Long?,
    @SerializedName("total_collections")
    val totalCollections: Long?,
    @SerializedName("followed_by_user")
    val followedByUser: Boolean?,
    @SerializedName("followers_count")
    val followersCount: Long?,
    @SerializedName("following_count")
    val followingCount: Long?,
    val downloads: Long?,
    @SerializedName("profile_image")
    val profileImage: UserProfileImageDTO?,
    val social: UserSocialMediaLinksDTO?,
    val badge: UserBadgeDTO?,
    val tags: UserTagsDTO?,
    val photos: List<PhotoDTO>?
) {
    fun toUser(): User = User(
        id = id,
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