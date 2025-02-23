package com.andrii_a.walleria.domain.repository

import com.andrii_a.walleria.domain.models.login.AccessToken
import com.andrii_a.walleria.domain.models.login.UserPrivateProfile
import com.andrii_a.walleria.domain.models.preferences.UserPrivateProfileData
import kotlinx.coroutines.flow.Flow

interface LocalAccountRepository {

    val isUserLoggedIn: Flow<Boolean>

    val accessToken: Flow<String>

    val userPrivateProfileData: Flow<UserPrivateProfileData>

    suspend fun saveAccessToken(accessToken: AccessToken)

    suspend fun saveAccountInfo(userPrivateProfile: UserPrivateProfile)

    suspend fun clearAccountInfo()

}