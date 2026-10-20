package com.andrii_a.walleria.data.repository.local

import com.andrii_a.walleria.domain.models.search.SearchHistoryItem
import com.andrii_a.walleria.domain.repository.SearchHistoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class FakeSearchHistoryRepository(
    initialHistory: List<SearchHistoryItem> = emptyList()
) : SearchHistoryRepository {

    private val _history = MutableStateFlow(initialHistory)

    override fun getSearchHistory(): Flow<List<SearchHistoryItem>> = _history.asStateFlow()

    override suspend fun getSearchHistoryItemByTitle(title: String): SearchHistoryItem? {
        return _history.value.find { it.title.equals(title, ignoreCase = true) }
    }

    override suspend fun insertItem(item: SearchHistoryItem) {
        _history.update { currentList ->
            listOf(item) + currentList.filterNot { it.title.equals(item.title, ignoreCase = true) }
        }
    }

    override suspend fun updateItem(item: SearchHistoryItem) {
        _history.update { currentList ->
            currentList.map { if (it.title.equals(item.title, ignoreCase = true)) item else it }
        }
    }

    override suspend fun deleteItem(item: SearchHistoryItem) {
        _history.update { currentList ->
            currentList.filterNot { it.title.equals(item.title, ignoreCase = true) }
        }
    }

    override suspend fun deleteAllItems() {
        _history.value = emptyList()
    }
}