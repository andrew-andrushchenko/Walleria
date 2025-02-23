package com.andrii_a.walleria.di

import com.andrii_a.walleria.data.remote.repository.LoginRepositoryImpl
import com.andrii_a.walleria.data.remote.services.LoginService
import com.andrii_a.walleria.data.remote.services.LoginServiceImpl
import com.andrii_a.walleria.domain.repository.LoginRepository
import com.andrii_a.walleria.domain.repository.UserAccountPreferencesRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LoginModule {

    /*@Provides
    @Singleton
    fun provideLoginService(retrofitBuilder: Retrofit.Builder): LoginService =
        retrofitBuilder.baseUrl(BASE_URL).build().create(LoginService::class.java)*/

    @Provides
    @Singleton
    fun provideLoginService(httpClient: HttpClient): LoginService = LoginServiceImpl(httpClient)

    @Provides
    @Singleton
    fun provideLoginRepository(
        loginService: LoginService,
        userAccountPreferencesRepository: UserAccountPreferencesRepository
    ): LoginRepository = LoginRepositoryImpl(loginService, userAccountPreferencesRepository)

}