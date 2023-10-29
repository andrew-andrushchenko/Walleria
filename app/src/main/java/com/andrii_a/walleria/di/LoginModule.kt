package com.andrii_a.walleria.di

import com.andrii_a.walleria.data.remote.repository.LoginRepositoryImpl
import com.andrii_a.walleria.data.remote.services.LoginService
import com.andrii_a.walleria.data.remote.services.UserService
import com.andrii_a.walleria.data.util.BASE_URL
import com.andrii_a.walleria.domain.repository.UserAccountPreferencesRepository
import com.andrii_a.walleria.domain.repository.LoginRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LoginModule {

    @Provides
    @Singleton
    fun provideLoginService(retrofitBuilder: Retrofit.Builder): LoginService =
        retrofitBuilder.baseUrl(BASE_URL).build().create(LoginService::class.java)

    @Provides
    @Singleton
    fun provideLoginRepository(
        loginService: LoginService,
        userService: UserService,
        userAccountPreferencesRepository: UserAccountPreferencesRepository
    ): LoginRepository = LoginRepositoryImpl(loginService, userService, userAccountPreferencesRepository)

}