package com.andrii_a.walleria.data.remote.repository

import com.andrii_a.walleria.data.remote.services.UserService
import com.andrii_a.walleria.domain.models.login.UserPrivateProfile
import com.andrii_a.walleria.domain.models.preferences.UserPrivateProfileData
import com.andrii_a.walleria.domain.models.user.User
import com.andrii_a.walleria.domain.network.Resource
import com.andrii_a.walleria.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class UserRepositoryImpl(private val userService: UserService) : UserRepository {

    override fun getUserPublicProfile(username: String): Flow<Resource<User>> = flow {
        emit(Resource.Loading)

        when (val result = userService.getUserPublicProfile(username)) {
            is Resource.Error -> emit(result)
            is Resource.Success -> emit(Resource.Success(result.value.toUser()))
            else -> Unit
        }
    }

    override fun getPrivateUserProfile(): Flow<Resource<UserPrivateProfile>> = flow {
        emit(Resource.Loading)

        when (val result = userService.getUserPrivateProfile()) {
            is Resource.Error -> emit(result)
            is Resource.Success -> emit(Resource.Success(result.value.toUserPrivateProfile()))
            else -> Unit
        }
    }

    override fun updatePrivateUserProfile(userPrivateProfileData: UserPrivateProfileData): Flow<Resource<UserPrivateProfile>> =
        flow {
            emit(Resource.Loading)

            val result = userService.updateUserPrivateProfile(
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