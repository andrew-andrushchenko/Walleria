package com.andrii_a.walleria.data.remote.dto.user

import com.andrii_a.walleria.domain.models.user.UserSocialMediaLinks
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserSocialMediaLinksDto(
    @SerialName("instagram_username")
    val instagramUsername: String? = null,
    @SerialName("portfolio_url")
    val portfolioUrl: String? = null,
    @SerialName("twitter_username")
    val twitterUsername: String? = null,
    @SerialName("paypal_email")
    val paypalEmail: String? = null
) {
    fun toUserSocial(): UserSocialMediaLinks = UserSocialMediaLinks(
        instagramUsername = instagramUsername,
        portfolioUrl = portfolioUrl,
        twitterUsername = twitterUsername,
        paypalEmail = paypalEmail
    )
}