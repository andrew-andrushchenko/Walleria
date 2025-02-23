package com.andrii_a.walleria.data.remote.repository

import com.andrii_a.walleria.data.remote.services.UserService
import com.andrii_a.walleria.domain.models.user.User
import com.andrii_a.walleria.domain.network.Resource
import com.andrii_a.walleria.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class UserRepositoryImpl(private val userService: UserService) : UserRepository {

    override fun getUserPublicProfile(username: String): Flow<Resource<User>> = flow {
        emit(Resource.Loading)

        when (val result = userService.getUserPublicProfile(username)) {
            is Resource.Error -> emit(result)
            is Resource.Success -> emit(Resource.Success(result.value.toUser()))
            else -> Unit
        }
    }
}