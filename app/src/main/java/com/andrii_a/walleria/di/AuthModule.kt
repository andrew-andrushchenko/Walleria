package com.andrii_a.walleria.di

import com.andrii_a.walleria.data.remote.repository.LoginRepositoryImpl
import com.andrii_a.walleria.data.remote.service.LoginService
import com.andrii_a.walleria.data.remote.service.UserService
import com.andrii_a.walleria.data.util.BASE_URL
import com.andrii_a.walleria.domain.repository.LocalUserAccountPreferencesRepository
import com.andrii_a.walleria.domain.repository.LoginRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthModule {

    @Provides
    @Singleton
    fun provideAuthorizationService(retrofitBuilder: Retrofit.Builder): LoginService =
        retrofitBuilder.baseUrl(BASE_URL).build().create(LoginService::class.java)

    @Provides
    @Singleton
    fun provideLoginRepository(
        loginService: LoginService,
        userService: UserService,
        localUserAccountPreferencesRepository: LocalUserAccountPreferencesRepository
    ): LoginRepository = LoginRepositoryImpl(loginService, userService, localUserAccountPreferencesRepository)

}