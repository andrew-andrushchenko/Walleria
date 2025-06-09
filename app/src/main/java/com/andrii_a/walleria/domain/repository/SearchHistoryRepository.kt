package com.andrii_a.walleria.domain.repository

import com.andrii_a.walleria.domain.models.search.SearchHistoryItem
import kotlinx.coroutines.flow.Flow

interface SearchHistoryRepository {

    fun getSearchHistory(): Flow<List<SearchHistoryItem>>

    suspend fun getSearchHistoryItemByTitle(title: String): SearchHistoryItem?

    suspend fun insertItem(item: SearchHistoryItem)

    suspend fun updateItem(item: SearchHistoryItem)

    suspend fun deleteItem(item: SearchHistoryItem)

    suspend fun deleteAllItems()

}