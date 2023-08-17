package com.andrii_a.walleria.data.local.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.andrii_a.walleria.domain.models.search.RecentSearchItem

@Entity("recent_searches_table")
data class RecentSearchItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val timeMillis: Long
) {
    fun toRecentSearchItem(): RecentSearchItem {
        return RecentSearchItem(
            id = id,
            title = title,
            timeMillis = timeMillis
        )
    }
}
