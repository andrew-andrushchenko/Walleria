package com.andrii_a.walleria.domain.repository

import com.andrii_a.walleria.core.BackendResult
import com.andrii_a.walleria.domain.models.user.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {

    suspend fun getUserPublicProfile(username: String): Flow<BackendResult<User>>

}