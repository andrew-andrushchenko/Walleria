package com.andrii_a.walleria.domain.repository

import com.andrii_a.walleria.core.BackendResult
import com.andrii_a.walleria.domain.models.login.AccessToken
import com.andrii_a.walleria.domain.models.login.MyProfile
import com.andrii_a.walleria.domain.models.preferences.MyProfileData
import kotlinx.coroutines.flow.Flow

interface LoginRepository {

    val loginUrl: String

    val joinUrl: String

    fun login(code: String): Flow<BackendResult<AccessToken>>

    suspend fun getAccessToken(code: String): BackendResult<AccessToken>

    suspend fun saveAccessToken(accessToken: AccessToken)

    suspend fun getMyProfile(): BackendResult<MyProfile>

    suspend fun saveMyProfile(myProfile: MyProfile)

    suspend fun updateMyProfile(myProfileData: MyProfileData): BackendResult<MyProfile>

    suspend fun logout()

}