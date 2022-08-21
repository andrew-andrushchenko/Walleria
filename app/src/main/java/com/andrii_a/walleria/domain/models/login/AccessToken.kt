package com.andrii_a.walleria.domain.models.login

data class AccessToken(
    val accessToken: String,
    val tokenType: String?,
    val scope: String?,
    val createAt: Int?
)