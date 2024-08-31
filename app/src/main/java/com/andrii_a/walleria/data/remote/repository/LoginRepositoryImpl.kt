package com.andrii_a.walleria.data.remote.repository

import com.andrii_a.walleria.data.remote.services.LoginService
import com.andrii_a.walleria.data.remote.services.UserService
import com.andrii_a.walleria.data.util.CLIENT_ID
import com.andrii_a.walleria.data.util.CLIENT_SECRET
import com.andrii_a.walleria.data.util.UNSPLASH_AUTH_CALLBACK
import com.andrii_a.walleria.data.util.WALLERIA_SCHEMA
import com.andrii_a.walleria.data.util.network.backendRequest
import com.andrii_a.walleria.data.util.network.backendRequestFlow
import com.andrii_a.walleria.domain.models.login.AccessToken
import com.andrii_a.walleria.domain.models.login.UserPrivateProfile
import com.andrii_a.walleria.domain.models.preferences.UserPrivateProfileData
import com.andrii_a.walleria.domain.network.Resource
import com.andrii_a.walleria.domain.repository.LoginRepository
import com.andrii_a.walleria.domain.repository.UserAccountPreferencesRepository
import kotlinx.coroutines.flow.Flow

class LoginRepositoryImpl(
    private val loginService: LoginService,
    private val userService: UserService,
    private val userAccountPreferencesRepository: UserAccountPreferencesRepository
) : LoginRepository {

    override val loginUrl: String
        get() = buildString {
            append("https://unsplash.com/oauth/authorize")
            append("?client_id=$CLIENT_ID")
            append("&redirect_uri=$WALLERIA_SCHEMA$UNSPLASH_AUTH_CALLBACK")
            append("&response_type=code")
            append("&scope=public+read_user+write_user+read_photos+write_photos")
            append("+write_likes+write_followers+read_collections+write_collections")
        }

    override val joinUrl: String
        get() = "https://unsplash.com/join"


    override fun login(code: String): Flow<Resource<AccessToken>> = backendRequestFlow {
        loginService.getAccessToken(
            clientId = CLIENT_ID,
            clientSecret = CLIENT_SECRET,
            redirectUri = "$WALLERIA_SCHEMA$UNSPLASH_AUTH_CALLBACK",
            code = code,
            grantType = "authorization_code"
        ).toAccessToken()
    }

    override suspend fun logout() = userAccountPreferencesRepository.clearAccountInfo()

    override suspend fun saveAccessToken(accessToken: AccessToken) {
        userAccountPreferencesRepository.saveAccessToken(accessToken)
    }

    override suspend fun getPrivateUserProfile(): Resource<UserPrivateProfile> =
        backendRequest {
            userService.getUserPrivateProfile().toUserPrivateProfile()
        }

    override suspend fun savePrivateUserProfile(userPrivateProfile: UserPrivateProfile) {
        userAccountPreferencesRepository.saveAccountInfo(userPrivateProfile)
    }

    override suspend fun updatePrivateUserProfile(
        userPrivateProfileData: UserPrivateProfileData
    ): Resource<UserPrivateProfile> =
        backendRequest {
            userService.updateUserPrivateProfile(
                username = userPrivateProfileData.nickname,
                firstName = userPrivateProfileData.firstName,
                lastName = userPrivateProfileData.lastName,
                email = userPrivateProfileData.email,
                url = userPrivateProfileData.portfolioLink,
                instagramUsername = userPrivateProfileData.instagramUsername,
                location = userPrivateProfileData.location,
                bio = userPrivateProfileData.bio
            ).toUserPrivateProfile()
        }
}