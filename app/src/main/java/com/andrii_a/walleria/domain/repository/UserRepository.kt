package com.andrii_a.walleria.domain.repository

import com.andrii_a.walleria.domain.models.login.UserPrivateProfile
import com.andrii_a.walleria.domain.models.preferences.UserPrivateProfileData
import com.andrii_a.walleria.domain.network.Resource
import com.andrii_a.walleria.domain.models.user.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {

    fun getUserPublicProfile(username: String): Flow<Resource<User>>

    fun getPrivateUserProfile(): Flow<Resource<UserPrivateProfile>>

    fun updatePrivateUserProfile(userPrivateProfileData: UserPrivateProfileData): Flow<Resource<UserPrivateProfile>>

}