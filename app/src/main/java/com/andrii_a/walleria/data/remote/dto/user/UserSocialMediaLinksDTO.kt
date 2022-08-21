package com.andrii_a.walleria.data.remote.dto.user

import com.andrii_a.walleria.domain.models.user.UserSocialMediaLinks
import com.google.gson.annotations.SerializedName

data class UserSocialMediaLinksDTO(
    @SerializedName("instagram_username")
    val instagramUsername: String?,
    @SerializedName("portfolio_url")
    val portfolioUrl: String?,
    @SerializedName("twitter_username")
    val twitterUsername: String?,
    @SerializedName("paypal_email")
    val paypalEmail: String?
) {
    fun toUserSocial(): UserSocialMediaLinks = UserSocialMediaLinks(
        instagramUsername = instagramUsername,
        portfolioUrl = portfolioUrl,
        twitterUsername = twitterUsername,
        paypalEmail = paypalEmail
    )
}