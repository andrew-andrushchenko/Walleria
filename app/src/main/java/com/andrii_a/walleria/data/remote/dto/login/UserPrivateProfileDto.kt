package com.andrii_a.walleria.data.remote.dto.login

import com.andrii_a.walleria.data.remote.dto.photo.PhotoDto
import com.andrii_a.walleria.data.remote.dto.photo.PhotoLinksDto
import com.andrii_a.walleria.data.remote.dto.user.UserProfileImageDto
import com.andrii_a.walleria.domain.models.login.UserPrivateProfile
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserPrivateProfileDto(
    val id: String? = null,
    @SerialName("updatedAt")
    val updatedAt: String? = null,
    val username: String? = null,
    @SerialName("first_name")
    val firstName: String? = null,
    @SerialName("last_name")
    val lastName: String? = null,
    @SerialName("twitter_username")
    val twitterUsername: String? = null,
    @SerialName("portfolio_url")
    val portfolioUrl: String? = null,
    val bio: String? = null,
    val location: String? = null,
    val links: PhotoLinksDto? = null,
    @SerialName("profile_image")
    val profileImage: UserProfileImageDto? = null,
    @SerialName("instagram_username")
    val instagramUsername: String? = null,
    @SerialName("total_likes")
    val totalLikes: Long? = null,
    @SerialName("total_photos")
    val totalPhotos: Long? = null,
    @SerialName("total_collections")
    val totalCollections: Long? = null,
    val photos: List<PhotoDto>? = null,
    @SerialName("followed_by_user")
    val followedByUser: Boolean? = null,
    @SerialName("followers_count")
    val followersCount: Long? = null,
    @SerialName("following_count")
    val followingCount: Long? = null,
    val downloads: Long? = null,
    @SerialName("uploads_remaining")
    val uploadsRemaining: Long? = null,
    val email: String? = null
) {
    fun toUserPrivateProfile(): UserPrivateProfile = UserPrivateProfile(
        id = id.orEmpty(),
        updatedAt = updatedAt,
        username = username.orEmpty(),
        firstName = firstName.orEmpty(),
        lastName = lastName.orEmpty(),
        twitterUsername = twitterUsername,
        portfolioUrl = portfolioUrl,
        bio = bio,
        location = location,
        links = links?.toPhotoLinks(),
        profileImage = profileImage?.toUserProfileImage(),
        instagramUsername = instagramUsername,
        totalLikes = totalLikes ?: 0,
        totalPhotos = totalPhotos ?: 0,
        totalCollections = totalCollections ?: 0,
        photos = photos?.map { it.toPhoto() },
        followedByUser = followedByUser ?: false,
        followersCount = followersCount ?: 0,
        followingCount = followingCount ?: 0,
        downloads = downloads ?: 0,
        email = email
    )
}
