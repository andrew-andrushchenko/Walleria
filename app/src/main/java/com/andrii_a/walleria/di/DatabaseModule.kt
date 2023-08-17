package com.andrii_a.walleria.di

import android.app.Application
import androidx.room.Room
import com.andrii_a.walleria.data.local.db.WalleriaDatabase
import com.andrii_a.walleria.data.local.db.dao.RecentSearchesDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideWalleriaDatabase(application: Application): WalleriaDatabase =
        Room.databaseBuilder(application, WalleriaDatabase::class.java, "walleria_db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideRecentSearchesDao(db: WalleriaDatabase): RecentSearchesDao = db.recentSearchesDao()

}