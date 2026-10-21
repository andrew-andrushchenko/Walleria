package com.andrii_a.walleria.data.repository.local

import com.andrii_a.walleria.domain.models.login.AccessToken
import com.andrii_a.walleria.domain.models.login.UserPrivateProfile
import com.andrii_a.walleria.domain.models.preferences.UserPrivateProfileData
import com.andrii_a.walleria.domain.repository.LocalAccountRepository
import kotlinx.coroutines.flow.MutableStateFlow

class FakeLocalAccountRepository(
    initialIsLoggedIn: Boolean = false,
    initialAccessToken: String = "",
    initialUserPrivateProfileData: UserPrivateProfileData = UserPrivateProfileData(),
) : LocalAccountRepository {

    override val isUserLoggedIn = MutableStateFlow(initialIsLoggedIn)
    override val accessToken = MutableStateFlow(initialAccessToken)
    override val userPrivateProfileData = MutableStateFlow(initialUserPrivateProfileData)

    override suspend fun saveAccessToken(accessToken: AccessToken) {
        this.accessToken.value = accessToken.value
        this.isUserLoggedIn.value = accessToken.value.isNotBlank()
    }

    override suspend fun saveAccountInfo(userPrivateProfile: UserPrivateProfile) {
        this.userPrivateProfileData.value = UserPrivateProfileData(
            nickname = userPrivateProfile.username,
            firstName = userPrivateProfile.firstName,
            lastName = userPrivateProfile.lastName,
            email = userPrivateProfile.email.orEmpty()
        )
    }

    override suspend fun clearAccountInfo() {
        this.isUserLoggedIn.value = false
        this.accessToken.value = ""
        this.userPrivateProfileData.value = UserPrivateProfileData()
    }
}
