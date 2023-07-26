package com.andrii_a.walleria.data.remote.dto.login

import com.andrii_a.walleria.domain.models.login.AccessToken
import com.google.gson.annotations.SerializedName

data class AccessTokenDTO(
    @SerializedName("access_token")
    val accessToken: String,
    @SerializedName("token_type")
    val tokenType: String?,
    val scope: String?,
    @SerializedName("create_at")
    val createAt: Int?
) {
    fun toAccessToken(): AccessToken = AccessToken(value = accessToken)
}
