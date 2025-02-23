package com.andrii_a.walleria.data.remote.services

import com.andrii_a.walleria.data.remote.dto.login.AccessTokenDto
import com.andrii_a.walleria.data.util.Endpoints
import com.andrii_a.walleria.data.util.backendRequest
import com.andrii_a.walleria.domain.network.Resource
import io.ktor.client.HttpClient
import io.ktor.client.request.post

class LoginServiceImpl(private val httpClient: HttpClient) : LoginService {

    override suspend fun getAccessToken(
        clientId: String,
        clientSecret: String,
        redirectUri: String,
        code: String,
        grantType: String
    ): Resource<AccessTokenDto> {
        return backendRequest {
            httpClient.post(Endpoints.ACCESS_TOKEN) {
                url {
                    parameters.append("client_id", clientId)
                    parameters.append("client_secret", clientSecret)
                    parameters.append("redirect_uri", redirectUri)
                    parameters.append("code", code)
                    parameters.append("grant_type", grantType)
                }
            }
        }
    }
}