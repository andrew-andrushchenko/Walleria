package com.andrii_a.walleria.data.local.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.andrii_a.walleria.data.local.db.entities.RecentSearchItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RecentSearchesDao {

    @Query("SELECT * FROM recent_searches_table ORDER BY timeMillis DESC")
    fun getRecentSearches(): Flow<List<RecentSearchItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(recentSearchItemEntity: RecentSearchItemEntity)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(recentSearchItemEntity: RecentSearchItemEntity)

    @Delete
    suspend fun delete(recentSearchItemEntity: RecentSearchItemEntity)

    @Query("DELETE FROM recent_searches_table")
    suspend fun deleteAll()

}