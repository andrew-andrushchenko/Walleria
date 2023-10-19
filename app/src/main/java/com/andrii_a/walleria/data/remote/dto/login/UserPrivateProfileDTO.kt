package com.andrii_a.walleria.data.remote.dto.login

import com.andrii_a.walleria.domain.models.login.UserPrivateProfile
import com.google.gson.annotations.SerializedName
import com.andrii_a.walleria.data.remote.dto.photo.PhotoDTO
import com.andrii_a.walleria.data.remote.dto.photo.PhotoLinksDTO
import com.andrii_a.walleria.data.remote.dto.user.UserProfileImageDTO

data class UserPrivateProfileDTO(
    val id: String,
    @SerializedName("updatedAt")
    val updatedAt: String?,
    val username: String?,
    @SerializedName("first_name")
    val firstName: String?,
    @SerializedName("last_name")
    val lastName: String?,
    @SerializedName("twitter_username")
    val twitterUsername: String?,
    @SerializedName("portfolio_url")
    val portfolioUrl: String?,
    val bio: String?,
    val location: String?,
    val links: PhotoLinksDTO?,
    @SerializedName("profile_image")
    val profileImage: UserProfileImageDTO?,
    @SerializedName("instagram_username")
    val instagramUsername: String?,
    @SerializedName("total_likes")
    val totalLikes: Long?,
    @SerializedName("total_photos")
    val totalPhotos: Long?,
    @SerializedName("total_collections")
    val totalCollections: Long?,
    val photos: List<PhotoDTO>?,
    @SerializedName("followed_by_user")
    val followedByUser: Boolean?,
    @SerializedName("followers_count")
    val followersCount: Long?,
    @SerializedName("following_count")
    val followingCount: Long?,
    val downloads: Long?,
    @SerializedName("uploads_remaining")
    val uploadsRemaining: Long?,
    val email: String?
) {
    fun toUserPrivateProfile(): UserPrivateProfile = UserPrivateProfile(
        id = id,
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
