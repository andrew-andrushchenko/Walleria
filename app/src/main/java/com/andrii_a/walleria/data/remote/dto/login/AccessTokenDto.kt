package com.andrii_a.walleria.data.remote.dto.login

import com.andrii_a.walleria.domain.models.login.AccessToken
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AccessTokenDto(
    @SerialName("access_token")
    val accessToken: String? = null,
    @SerialName("token_type")
    val tokenType: String? = null,
    val scope: String? = null,
    @SerialName("create_at")
    val createAt: Int? = null
) {
    fun toAccessToken(): AccessToken = AccessToken(value = accessToken.orEmpty())
}
