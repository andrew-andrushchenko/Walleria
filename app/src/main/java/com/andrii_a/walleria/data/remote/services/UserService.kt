package com.andrii_a.walleria.data.remote.services

import com.andrii_a.walleria.data.remote.dto.login.UserPrivateProfileDTO
import com.andrii_a.walleria.data.remote.dto.user.UserDTO
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface UserService {

    @GET("users/{username}")
    suspend fun getUserPublicProfile(
        @Path("username") username: String
    ): UserDTO

    @GET("me")
    suspend fun getUserPrivateProfile(): UserPrivateProfileDTO

    @PUT("me")
    suspend fun updateUserPrivateProfile(
        @Query("username") username: String?,
        @Query("first_name") firstName: String?,
        @Query("last_name") lastName: String?,
        @Query("email") email: String?,
        @Query("url") url: String?,
        @Query("instagram_username") instagramUsername: String?,
        @Query("location") location: String?,
        @Query("bio") bio: String?
    ): UserPrivateProfileDTO
}