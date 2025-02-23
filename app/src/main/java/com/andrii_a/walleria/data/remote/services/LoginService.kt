package com.andrii_a.walleria.data.remote.services

import com.andrii_a.walleria.data.remote.dto.login.AccessTokenDto
import com.andrii_a.walleria.domain.network.Resource

interface LoginService {

    suspend fun getAccessToken(
        clientId: String,
        clientSecret: String,
        redirectUri: String,
        code: String,
        grantType: String
    ): Resource<AccessTokenDto>
}