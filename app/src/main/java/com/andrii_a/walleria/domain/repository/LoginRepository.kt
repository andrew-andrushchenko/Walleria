package com.andrii_a.walleria.domain.repository

import com.andrii_a.walleria.domain.models.login.AccessToken
import com.andrii_a.walleria.domain.network.Resource
import kotlinx.coroutines.flow.Flow

interface LoginRepository {

    fun login(code: String): Flow<Resource<AccessToken>>

}