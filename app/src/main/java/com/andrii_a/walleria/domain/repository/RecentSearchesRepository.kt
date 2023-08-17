package com.andrii_a.walleria.domain.repository

import com.andrii_a.walleria.domain.models.search.RecentSearchItem
import kotlinx.coroutines.flow.Flow

interface RecentSearchesRepository {

    fun getAllRecentSearches(): Flow<List<RecentSearchItem>>

    suspend fun insertItem(item: RecentSearchItem)

    suspend fun updateItem(item: RecentSearchItem)

    suspend fun deleteItem(item: RecentSearchItem)

    suspend fun deleteAllItems()

}