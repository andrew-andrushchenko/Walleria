package com.andrii_a.walleria.data.local.repository

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.andrii_a.walleria.domain.models.login.AccessToken
import com.andrii_a.walleria.domain.models.login.MyProfile
import com.andrii_a.walleria.domain.models.preferences.MyProfileData
import com.andrii_a.walleria.domain.repository.UserAccountPreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private const val TAG = "LocalUserAccountPrefere"

private val Context.localUserAccountDataStore: DataStore<Preferences> by preferencesDataStore(name = "user_account_data_store")

class UserAccountPreferencesRepositoryImpl(context: Context) : UserAccountPreferencesRepository {

    private val localUserAccountDataStore: DataStore<Preferences> by lazy {
        return@lazy context.localUserAccountDataStore
    }

    private val preferencesFlow = localUserAccountDataStore.data
        .catch { exception ->
            if (exception is IOException) {
                Log.e(TAG, "Error reading preferences", exception)
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }

    override val isUserAuthorized: Flow<Boolean> = preferencesFlow.map { preferences ->
        preferences[UserAccountPreferencesKeys.ACCESS_TOKEN_KEY].isNullOrBlank().not()
    }

    override val accessToken: Flow<String> = preferencesFlow.map { preferences ->
        preferences[UserAccountPreferencesKeys.ACCESS_TOKEN_KEY].orEmpty()
    }

    override val myProfileData: Flow<MyProfileData> = preferencesFlow.map { preferences ->
        val nickname = preferences[UserAccountPreferencesKeys.USER_NICKNAME_KEY].orEmpty()
        val firstName = preferences[UserAccountPreferencesKeys.USER_FIRST_NAME_KEY].orEmpty()
        val lastName = preferences[UserAccountPreferencesKeys.USER_LAST_NAME_KEY].orEmpty()
        val profilePhotoUrl = preferences[UserAccountPreferencesKeys.USER_PROFILE_PHOTO_URL_KEY].orEmpty()
        val email = preferences[UserAccountPreferencesKeys.USER_EMAIL_KEY].orEmpty()
        val portfolioLink = preferences[UserAccountPreferencesKeys.USER_PORTFOLIO_LINK_KEY].orEmpty()
        val instagramUsername = preferences[UserAccountPreferencesKeys.USER_INSTAGRAM_USERNAME_KEY].orEmpty()
        val location = preferences[UserAccountPreferencesKeys.USER_LOCATION_KEY].orEmpty()
        val bio = preferences[UserAccountPreferencesKeys.USER_BIO_KEY].orEmpty()

        MyProfileData(
            nickname = nickname,
            firstName = firstName,
            lastName = lastName,
            profilePhotoUrl = profilePhotoUrl,
            email = email,
            portfolioLink = portfolioLink,
            instagramUsername = instagramUsername,
            location = location,
            bio = bio
        )
    }

    override suspend fun saveAccessToken(accessToken: AccessToken) {
        localUserAccountDataStore.edit { preferences ->
            preferences[UserAccountPreferencesKeys.ACCESS_TOKEN_KEY] = accessToken.value
        }
    }

    override suspend fun saveMyProfileInfo(myProfile: MyProfile) {
        localUserAccountDataStore.edit { preferences ->
            preferences[UserAccountPreferencesKeys.USER_NICKNAME_KEY] = myProfile.username
            preferences[UserAccountPreferencesKeys.USER_FIRST_NAME_KEY] = myProfile.firstName
            preferences[UserAccountPreferencesKeys.USER_LAST_NAME_KEY] = myProfile.lastName
            preferences[UserAccountPreferencesKeys.USER_PROFILE_PHOTO_URL_KEY] = myProfile.profileImage?.medium.orEmpty()
            preferences[UserAccountPreferencesKeys.USER_EMAIL_KEY] = myProfile.email.orEmpty()
            preferences[UserAccountPreferencesKeys.USER_PORTFOLIO_LINK_KEY] = myProfile.portfolioUrl.orEmpty()
            preferences[UserAccountPreferencesKeys.USER_INSTAGRAM_USERNAME_KEY] = myProfile.instagramUsername.orEmpty()
            preferences[UserAccountPreferencesKeys.USER_LOCATION_KEY] = myProfile.location.orEmpty()
            preferences[UserAccountPreferencesKeys.USER_BIO_KEY] = myProfile.bio.orEmpty()
        }
    }

    override suspend fun reset() {
        localUserAccountDataStore.edit { preferences ->
            preferences[UserAccountPreferencesKeys.ACCESS_TOKEN_KEY] = ""
            preferences[UserAccountPreferencesKeys.USER_NICKNAME_KEY] = ""
            preferences[UserAccountPreferencesKeys.USER_FIRST_NAME_KEY] = ""
            preferences[UserAccountPreferencesKeys.USER_LAST_NAME_KEY] = ""
            preferences[UserAccountPreferencesKeys.USER_PROFILE_PHOTO_URL_KEY] = ""
            preferences[UserAccountPreferencesKeys.USER_EMAIL_KEY] = ""
            preferences[UserAccountPreferencesKeys.USER_PORTFOLIO_LINK_KEY] = ""
            preferences[UserAccountPreferencesKeys.USER_INSTAGRAM_USERNAME_KEY] = ""
            preferences[UserAccountPreferencesKeys.USER_LOCATION_KEY] = ""
            preferences[UserAccountPreferencesKeys.USER_BIO_KEY] = ""
        }
    }

    object UserAccountPreferencesKeys {

        val ACCESS_TOKEN_KEY = stringPreferencesKey("access_token")

        val USER_NICKNAME_KEY = stringPreferencesKey("user_nickname")

        val USER_FIRST_NAME_KEY = stringPreferencesKey("user_first_name")

        val USER_LAST_NAME_KEY = stringPreferencesKey("user_last_name")

        val USER_PROFILE_PHOTO_URL_KEY = stringPreferencesKey("user_profile_photo_url")

        val USER_EMAIL_KEY = stringPreferencesKey("user_email")

        val USER_PORTFOLIO_LINK_KEY = stringPreferencesKey("user_portfolio_link")

        val USER_INSTAGRAM_USERNAME_KEY = stringPreferencesKey("user_instagram_username")

        val USER_LOCATION_KEY = stringPreferencesKey("user_location")

        val USER_BIO_KEY = stringPreferencesKey("user_bio")
    }
}