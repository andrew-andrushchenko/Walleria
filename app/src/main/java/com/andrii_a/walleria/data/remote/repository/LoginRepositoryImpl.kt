package com.andrii_a.walleria.data.remote.repository

import com.andrii_a.walleria.data.remote.services.LoginService
import com.andrii_a.walleria.data.util.Config
import com.andrii_a.walleria.domain.models.login.AccessToken
import com.andrii_a.walleria.domain.network.Resource
import com.andrii_a.walleria.domain.repository.LoginRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class LoginRepositoryImpl(private val loginService: LoginService) : LoginRepository {

    override fun login(code: String): Flow<Resource<AccessToken>> = flow {
        emit(Resource.Loading)

        val result = loginService.getAccessToken(
            clientId = Config.CLIENT_ID,
            clientSecret = Config.CLIENT_SECRET,
            redirectUri = Config.AUTH_CALLBACK,
            code = code,
            grantType = Config.AUTH_GRANT_TYPE
        )

        when (result) {
            is Resource.Error -> emit(result)
            is Resource.Success -> emit(Resource.Success(result.value.toAccessToken()))
            else -> Unit
        }
    }
}