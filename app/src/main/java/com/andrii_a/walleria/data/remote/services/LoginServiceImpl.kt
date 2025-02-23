package com.andrii_a.walleria.data.remote.services

import com.andrii_a.walleria.data.remote.dto.login.AccessTokenDto
import com.andrii_a.walleria.data.remote.dto.login.UserPrivateProfileDto
import com.andrii_a.walleria.data.util.Endpoints
import com.andrii_a.walleria.data.util.backendRequest
import com.andrii_a.walleria.domain.network.Resource
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put

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

    override suspend fun getUserPrivateProfile(): Resource<UserPrivateProfileDto> {
        return backendRequest {
            httpClient.get(Endpoints.USER_PRIVATE_PROFILE)
        }
    }

    override suspend fun updateUserPrivateProfile(
        username: String?,
        firstName: String?,
        lastName: String?,
        email: String?,
        url: String?,
        instagramUsername: String?,
        location: String?,
        bio: String?
    ): Resource<UserPrivateProfileDto> {
        return backendRequest {
            httpClient.put(Endpoints.USER_PRIVATE_PROFILE) {
                url {
                    parameters.append("username", username.orEmpty())
                    parameters.append("first_name", firstName.orEmpty())
                    parameters.append("last_name", lastName.orEmpty())
                    parameters.append("email", email.orEmpty())
                    parameters.append("url", url.orEmpty())
                    parameters.append("instagram_username", instagramUsername.orEmpty())
                    parameters.append("location", location.orEmpty())
                    parameters.append("bio", bio.orEmpty())
                }
            }
        }
    }
}