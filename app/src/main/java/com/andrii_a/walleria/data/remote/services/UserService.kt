package com.andrii_a.walleria.data.remote.services

import com.andrii_a.walleria.data.remote.dto.user.UserDto
import com.andrii_a.walleria.domain.network.Resource

interface UserService {

    suspend fun getUserPublicProfile(username: String): Resource<UserDto>

}