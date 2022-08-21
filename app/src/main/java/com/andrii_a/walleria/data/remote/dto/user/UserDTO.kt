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
    val totalLikes: Int?,
    @SerializedName("total_photos")
    val totalPhotos: Int?,
    @SerializedName("total_collections")
    val totalCollections: Int?,
    @SerializedName("followed_by_user")
    val followedByUser: Boolean?,
    @SerializedName("followers_count")
    val followersCount: Int?,
    @SerializedName("following_count")
    val followingCount: Int?,
    val downloads: Int?,
    @SerializedName("profile_image")
    val profileImage: UserProfileImageDTO?,
    val social: UserSocialMediaLinksDTO?,
    val badge: UserBadgeDTO?,
    val tags: UserTagsDTO?,
    val photos: List<PhotoDTO>?
) {
    fun toUser(): User = User(
        id = id,
        username = username,
        name = name,
        firstName = firstName,
        lastName = lastName,
        bio = bio,
        location = location,
        totalLikes = totalLikes,
        totalPhotos = totalPhotos,
        totalCollections = totalCollections,
        followersCount = followersCount,
        followingCount = followingCount,
        downloads = downloads,
        profileImage = profileImage?.toUserProfileImage(),
        social = social?.toUserSocial(),
        badge = badge?.toUserBadge(),
        tags = tags?.toUserTags(),
        photos = photos?.map { it.toPhoto() }
    )
}