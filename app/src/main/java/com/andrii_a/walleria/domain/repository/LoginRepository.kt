package com.andrii_a.walleria.domain.repository

import com.andrii_a.walleria.core.BackendResult
import com.andrii_a.walleria.domain.models.login.AccessToken
import com.andrii_a.walleria.domain.models.login.MyProfile
import kotlinx.coroutines.flow.Flow

interface LoginRepository {

    val loginUrl: String

    val joinUrl: String

    fun login(code: String): Flow<BackendResult<AccessToken>>

    suspend fun getAccessToken(code: String): BackendResult<AccessToken>

    suspend fun saveAccessToken(accessToken: AccessToken)

    suspend fun getMyProfile(): BackendResult<MyProfile>

    suspend fun saveMyProfile(myProfile: MyProfile)

    suspend fun updateMyProfile(
        username: String?,
        firstName: String?,
        lastName: String?,
        email: String?,
        url: String?,
        instagramUsername: String?,
        location: String?,
        bio: String?
    ): BackendResult<MyProfile>

    suspend fun logout()

}