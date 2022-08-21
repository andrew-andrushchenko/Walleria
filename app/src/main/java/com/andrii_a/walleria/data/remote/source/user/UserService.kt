package com.andrii_a.walleria.data.remote.source.user

import com.andrii_a.walleria.data.remote.dto.login.MyProfileDTO
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
    suspend fun getMyProfile(): MyProfileDTO

    @PUT("me")
    suspend fun updateMyProfile(
        @Query("username") username: String?,
        @Query("first_name") firstName: String?,
        @Query("last_name") lastName: String?,
        @Query("email") email: String?,
        @Query("url") url: String?,
        @Query("instagram_username") instagramUsername: String?,
        @Query("location") location: String?,
        @Query("bio") bio: String?
    ): MyProfileDTO
}