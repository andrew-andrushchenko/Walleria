package com.andrii_a.walleria.data.remote.services

import com.andrii_a.walleria.data.remote.dto.user.UserDto
import com.andrii_a.walleria.data.util.Endpoints
import com.andrii_a.walleria.data.util.backendRequest
import com.andrii_a.walleria.domain.network.Resource
import io.ktor.client.HttpClient
import io.ktor.client.request.get

class UserServiceImpl(private val httpClient: HttpClient) : UserService {

    override suspend fun getUserPublicProfile(username: String): Resource<UserDto> {
        return backendRequest {
            httpClient.get(Endpoints.singleUser(username))
        }
    }
}