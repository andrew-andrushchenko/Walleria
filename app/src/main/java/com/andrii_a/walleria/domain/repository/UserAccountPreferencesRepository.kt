package com.andrii_a.walleria.domain.repository

import com.andrii_a.walleria.domain.models.login.AccessToken
import com.andrii_a.walleria.domain.models.login.MyProfile
import com.andrii_a.walleria.domain.models.preferences.MyProfileData
import kotlinx.coroutines.flow.Flow

interface UserAccountPreferencesRepository {

    val isUserAuthorized: Flow<Boolean>

    val accessToken: Flow<String>

    val myProfileData: Flow<MyProfileData>

    suspend fun saveAccessToken(accessToken: AccessToken)

    suspend fun saveMyProfileInfo(myProfile: MyProfile)

    suspend fun reset()

}