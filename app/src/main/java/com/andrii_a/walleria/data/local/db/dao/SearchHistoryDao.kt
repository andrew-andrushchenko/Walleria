package com.andrii_a.walleria.data.local.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.andrii_a.walleria.data.local.db.entities.SearchHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SearchHistoryDao {

    @Query("SELECT * FROM search_history_table ORDER BY timeMillis DESC")
    fun getSearchHistory(): Flow<List<SearchHistoryEntity>>

    @Query("SELECT * FROM search_history_table WHERE title = :title LIMIT 1")
    suspend fun getSearchHistoryItemByTitle(title: String) : SearchHistoryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: SearchHistoryEntity)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(entity: SearchHistoryEntity)

    @Delete
    suspend fun delete(searchHistoryEntity: SearchHistoryEntity)

    @Query("DELETE FROM search_history_table")
    suspend fun deleteAll()

}