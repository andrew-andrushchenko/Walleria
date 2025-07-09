package com.andrii_a.walleria.data.local.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.andrii_a.walleria.domain.models.search.SearchHistoryItem

@Entity("search_history_table")
data class SearchHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val timeMillis: Long
) {
    fun toSearchHistoryItem(): SearchHistoryItem {
        return SearchHistoryItem(
            id = id,
            title = title,
            timeMillis = timeMillis
        )
    }
}
