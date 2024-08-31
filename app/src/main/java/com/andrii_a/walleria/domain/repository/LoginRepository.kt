package com.andrii_a.walleria.domain.repository

import com.andrii_a.walleria.domain.network.Resource
import com.andrii_a.walleria.domain.models.login.AccessToken
import com.andrii_a.walleria.domain.models.login.UserPrivateProfile
import com.andrii_a.walleria.domain.models.preferences.UserPrivateProfileData
import kotlinx.coroutines.flow.Flow

interface LoginRepository {

    val loginUrl: String

    val joinUrl: String

    fun login(code: String): Flow<Resource<AccessToken>>

    suspend fun logout()

    suspend fun saveAccessToken(accessToken: AccessToken)

    suspend fun getPrivateUserProfile(): Resource<UserPrivateProfile>

    suspend fun savePrivateUserProfile(userPrivateProfile: UserPrivateProfile)

    suspend fun updatePrivateUserProfile(userPrivateProfileData: UserPrivateProfileData): Resource<UserPrivateProfile>

}