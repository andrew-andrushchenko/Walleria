package com.andrii_a.walleria.di

import com.andrii_a.walleria.data.util.network.AccessTokenInterceptor
import com.andrii_a.walleria.domain.repository.UserAccountPreferencesRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object GeneralNetworkModule {

    @Provides
    @Singleton
    fun provideAccessTokenInterceptor(
        userAccountPreferencesRepository: UserAccountPreferencesRepository
    ): AccessTokenInterceptor = AccessTokenInterceptor(userAccountPreferencesRepository)

    @Provides
    @Singleton
    fun provideOkHttpClient(accessTokenInterceptor: AccessTokenInterceptor): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(accessTokenInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

    @Provides
    @Singleton
    fun provideRetrofitBuilder(okHttpClient: OkHttpClient): Retrofit.Builder =
        Retrofit.Builder()
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())

}