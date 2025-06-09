package com.andrii_a.walleria.di

import androidx.room.Room
import com.andrii_a.walleria.data.local.db.WalleriaDatabase
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val databaseModule = module {
    single<WalleriaDatabase> {
        Room.databaseBuilder(androidApplication(), WalleriaDatabase::class.java, "walleria_db")
            .fallbackToDestructiveMigration(true)
            .build()
    }

    factory { get<WalleriaDatabase>().searchHistoryDao() }
}
