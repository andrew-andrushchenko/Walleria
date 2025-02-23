package com.andrii_a.walleria.data.remote.repository

import com.andrii_a.walleria.data.remote.services.LoginService
import com.andrii_a.walleria.data.util.Config
import com.andrii_a.walleria.domain.models.login.AccessToken
import com.andrii_a.walleria.domain.models.login.UserPrivateProfile
import com.andrii_a.walleria.domain.models.preferences.UserPrivateProfileData
import com.andrii_a.walleria.domain.network.Resource
import com.andrii_a.walleria.domain.repository.LoginRepository
import com.andrii_a.walleria.domain.repository.UserAccountPreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class LoginRepositoryImpl(
    private val loginService: LoginService,
    private val userAccountPreferencesRepository: UserAccountPreferencesRepository
) : LoginRepository {

    override val loginUrl: String
        get() = Config.LOGIN_URL

    override val joinUrl: String
        get() = Config.JOIN_URL


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

    override suspend fun logout() = userAccountPreferencesRepository.clearAccountInfo()

    override suspend fun saveAccessToken(accessToken: AccessToken) {
        userAccountPreferencesRepository.saveAccessToken(accessToken)
    }

    override fun getPrivateUserProfile(): Flow<Resource<UserPrivateProfile>> = flow {
        emit(Resource.Loading)

        when (val result = loginService.getUserPrivateProfile()) {
            is Resource.Error -> emit(result)
            is Resource.Success -> emit(Resource.Success(result.value.toUserPrivateProfile()))
            else -> Unit
        }
    }

    override suspend fun savePrivateUserProfile(userPrivateProfile: UserPrivateProfile) {
        userAccountPreferencesRepository.saveAccountInfo(userPrivateProfile)
    }

    override fun updatePrivateUserProfile(userPrivateProfileData: UserPrivateProfileData): Flow<Resource<UserPrivateProfile>> =
        flow {
            emit(Resource.Loading)

            val result = loginService.updateUserPrivateProfile(
                username = userPrivateProfileData.nickname,
                firstName = userPrivateProfileData.firstName,
                lastName = userPrivateProfileData.lastName,
                email = userPrivateProfileData.email,
                url = userPrivateProfileData.portfolioLink,
                instagramUsername = userPrivateProfileData.instagramUsername,
                location = userPrivateProfileData.location,
                bio = userPrivateProfileData.bio
            )

            when (result) {
                is Resource.Error -> emit(result)
                is Resource.Success -> emit(Resource.Success(result.value.toUserPrivateProfile()))
                else -> Unit
            }
        }
}