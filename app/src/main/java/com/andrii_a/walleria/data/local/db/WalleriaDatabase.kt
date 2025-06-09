package com.andrii_a.walleria.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.andrii_a.walleria.data.local.db.dao.SearchHistoryDao
import com.andrii_a.walleria.data.local.db.entities.SearchHistoryEntity

@Database(entities = [SearchHistoryEntity::class], version = 1)
abstract class WalleriaDatabase : RoomDatabase() {
    abstract fun searchHistoryDao(): SearchHistoryDao
}