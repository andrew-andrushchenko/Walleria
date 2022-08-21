package com.andrii_a.walleria.domain.repository

import com.andrii_a.walleria.data.util.network.BackendResult
import com.andrii_a.walleria.domain.models.user.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {

    suspend fun getUserPublicProfile(username: String): Flow<BackendResult<User>>

}