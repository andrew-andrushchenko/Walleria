package com.andrii_a.walleria.domain.repository

import com.andrii_a.walleria.domain.network.BackendResult
import com.andrii_a.walleria.domain.models.user.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {

    fun getUserPublicProfile(username: String): Flow<BackendResult<User>>

}