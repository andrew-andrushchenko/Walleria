package com.andrii_a.walleria.data.remote.dto.login

import com.andrii_a.walleria.domain.models.login.MyProfile
import com.google.gson.annotations.SerializedName
import com.andrii_a.walleria.data.remote.dto.photo.PhotoDTO
import com.andrii_a.walleria.data.remote.dto.photo.PhotoLinksDTO
import com.andrii_a.walleria.data.remote.dto.user.UserProfileImageDTO

data class MyProfileDTO(
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
    val totalLikes: Int?,
    @SerializedName("total_photos")
    val totalPhotos: Int?,
    @SerializedName("total_collections")
    val totalCollections: Int?,
    val photos: List<PhotoDTO>?,
    @SerializedName("followed_by_user")
    val followedByUser: Boolean?,
    @SerializedName("followers_count")
    val followersCount: Int?,
    @SerializedName("following_count")
    val followingCount: Int?,
    val downloads: Int?,
    @SerializedName("uploads_remaining")
    val uploadsRemaining: Int?,
    val email: String?
) {
    fun toMyProfile(): MyProfile = MyProfile(
        id = id,
        updatedAt = updatedAt,
        username = username,
        firstName = firstName,
        lastName = lastName,
        twitterUsername = twitterUsername,
        portfolioUrl = portfolioUrl,
        bio = bio,
        location = location,
        links = links?.toPhotoLinks(),
        profileImage = profileImage?.toUserProfileImage(),
        instagramUsername = instagramUsername,
        totalLikes = totalLikes,
        totalPhotos = totalPhotos,
        totalCollections = totalCollections,
        photos = photos?.map { it.toPhoto() },
        followedByUser = followedByUser,
        followersCount = followersCount,
        followingCount = followingCount,
        downloads = downloads,
        uploadsRemaining = uploadsRemaining,
        email = email
    )
}
