package com.andrii_a.walleria.data.remote.services

import com.andrii_a.walleria.data.remote.dto.login.UserPrivateProfileDto
import com.andrii_a.walleria.data.remote.dto.user.UserDto
import com.andrii_a.walleria.domain.network.Resource

interface UserService {

    suspend fun getUserPublicProfile(username: String): Resource<UserDto>

    suspend fun getUserPrivateProfile(): Resource<UserPrivateProfileDto>

    suspend fun updateUserPrivateProfile(
        username: String?,
        firstName: String?,
        lastName: String?,
        email: String?,
        url: String?,
        instagramUsername: String?,
        location: String?,
        bio: String?
    ): Resource<UserPrivateProfileDto>

}