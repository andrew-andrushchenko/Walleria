package com.andrii_a.walleria.data.remote.repository

import com.andrii_a.walleria.data.remote.services.LoginService
import com.andrii_a.walleria.data.remote.services.UserService
import com.andrii_a.walleria.data.util.CLIENT_ID
import com.andrii_a.walleria.data.util.CLIENT_SECRET
import com.andrii_a.walleria.data.util.UNSPLASH_AUTH_CALLBACK
import com.andrii_a.walleria.data.util.WALLERIA_SCHEMA
import com.andrii_a.walleria.domain.network.BackendResult
import com.andrii_a.walleria.data.util.network.backendRequest
import com.andrii_a.walleria.domain.models.login.AccessToken
import com.andrii_a.walleria.domain.models.login.MyProfile
import com.andrii_a.walleria.domain.models.preferences.MyProfileData
import com.andrii_a.walleria.domain.repository.UserAccountPreferencesRepository
import com.andrii_a.walleria.domain.repository.LoginRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

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


    override fun login(code: String): Flow<BackendResult<AccessToken>> {
        return flow {
            emit(BackendResult.Loading)
            emit(getAccessToken(code))
        }
    }

    override suspend fun getAccessToken(code: String): BackendResult<AccessToken> =
        backendRequest {
            loginService.getAccessToken(
                clientId = CLIENT_ID,
                clientSecret = CLIENT_SECRET,
                redirectUri = "$WALLERIA_SCHEMA$UNSPLASH_AUTH_CALLBACK",
                code = code,
                grantType = "authorization_code"
            ).toAccessToken()
        }

    override suspend fun saveAccessToken(accessToken: AccessToken) {
        userAccountPreferencesRepository.saveAccessToken(accessToken)
    }

    override suspend fun getMyProfile(): BackendResult<MyProfile> = backendRequest {
        userService.getMyProfile().toMyProfile()
    }

    override suspend fun saveMyProfile(myProfile: MyProfile) {
        userAccountPreferencesRepository.saveMyProfileInfo(myProfile)
    }

    override suspend fun updateMyProfile(myProfileData: MyProfileData): BackendResult<MyProfile> =
        backendRequest {
            userService.updateMyProfile(
                username = myProfileData.nickname,
                firstName = myProfileData.firstName,
                lastName = myProfileData.lastName,
                email = myProfileData.email,
                url = myProfileData.portfolioLink,
                instagramUsername = myProfileData.instagramUsername,
                location = myProfileData.location,
                bio = myProfileData.bio
            ).toMyProfile()
        }

    override suspend fun logout() = userAccountPreferencesRepository.reset()

}