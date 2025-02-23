package com.andrii_a.walleria.di

import com.andrii_a.walleria.data.remote.repository.UserRepositoryImpl
import com.andrii_a.walleria.data.remote.services.UserService
import com.andrii_a.walleria.data.remote.services.UserServiceImpl
import com.andrii_a.walleria.domain.repository.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UserModule {

    /*@Provides
    @Singleton
    fun provideUserService(retrofitBuilder: Retrofit.Builder): UserService =
        retrofitBuilder.baseUrl(BASE_API_URL).build().create(UserService::class.java)*/

    @Provides
    @Singleton
    fun provideUserService(httpClient: HttpClient): UserService = UserServiceImpl(httpClient)

    @Provides
    @Singleton
    fun provideUserRepository(userService: UserService): UserRepository = UserRepositoryImpl(userService)

}