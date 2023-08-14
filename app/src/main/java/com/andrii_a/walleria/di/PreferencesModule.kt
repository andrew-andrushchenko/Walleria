package com.andrii_a.walleria.di

import android.content.Context
import com.andrii_a.walleria.data.local.repository.LocalPreferencesRepositoryImpl
import com.andrii_a.walleria.data.local.repository.LocalUserAccountPreferencesRepositoryImpl
import com.andrii_a.walleria.domain.repository.LocalPreferencesRepository
import com.andrii_a.walleria.domain.repository.LocalUserAccountPreferencesRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PreferencesModule {

    @Provides
    @Singleton
    fun provideLocalUserAccountPreferencesRepository(
        @ApplicationContext context: Context
    ): LocalUserAccountPreferencesRepository = LocalUserAccountPreferencesRepositoryImpl(context)

    @Provides
    @Singleton
    fun provideLocalPreferencesRepository(
        @ApplicationContext context: Context
    ): LocalPreferencesRepository = LocalPreferencesRepositoryImpl(context)
}