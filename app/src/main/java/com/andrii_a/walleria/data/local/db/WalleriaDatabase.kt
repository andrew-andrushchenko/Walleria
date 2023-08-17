package com.andrii_a.walleria.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.andrii_a.walleria.data.local.db.dao.RecentSearchesDao
import com.andrii_a.walleria.data.local.db.entities.RecentSearchItemEntity

@Database(entities = [RecentSearchItemEntity::class], version = 1)
abstract class WalleriaDatabase : RoomDatabase() {
    abstract fun recentSearchesDao(): RecentSearchesDao
}