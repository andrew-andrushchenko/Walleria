package com.andrii_a.walleria.data.remote.source.login

import com.andrii_a.walleria.data.remote.dto.login.AccessTokenDTO
import retrofit2.http.POST
import retrofit2.http.Query

interface LoginService {

    @POST("oauth/token")
    suspend fun getAccessToken(
        @Query("client_id") clientId: String,
        @Query("client_secret") clientSecret: String,
        @Query("redirect_uri") redirectUri: String,
        @Query("code") code: String,
        @Query("grant_type") grantType: String
    ): AccessTokenDTO
}