package com.andrii_a.walleria.data.remote.repository

import com.andrii_a.walleria.data.remote.service.UserService
import com.andrii_a.walleria.core.BackendResult
import com.andrii_a.walleria.data.util.network.backendRequestFlow
import com.andrii_a.walleria.domain.models.user.User
import com.andrii_a.walleria.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow

class UserRepositoryImpl(private val userService: UserService) : UserRepository {

    override suspend fun getUserPublicProfile(username: String): Flow<BackendResult<User>> =
        backendRequestFlow {
            userService.getUserPublicProfile(username).toUser()
        }
}