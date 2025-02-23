package com.andrii_a.walleria.data.remote.services

import com.andrii_a.walleria.data.remote.dto.login.UserPrivateProfileDto
import com.andrii_a.walleria.data.remote.dto.user.UserDto
import com.andrii_a.walleria.data.util.Endpoints
import com.andrii_a.walleria.data.util.backendRequest
import com.andrii_a.walleria.domain.network.Resource
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.put

class UserServiceImpl(private val httpClient: HttpClient) : UserService {

    override suspend fun getUserPublicProfile(username: String): Resource<UserDto> {
        return backendRequest {
            httpClient.get(Endpoints.singleUser(username))
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