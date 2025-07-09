package com.andrii_a.walleria.data.local.repository

import com.andrii_a.walleria.data.local.db.dao.SearchHistoryDao
import com.andrii_a.walleria.data.local.db.entities.SearchHistoryEntity
import com.andrii_a.walleria.domain.models.search.SearchHistoryItem
import com.andrii_a.walleria.domain.repository.SearchHistoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow

class SearchHistoryRepositoryImpl(
    private val searchHistoryDao: SearchHistoryDao
) : SearchHistoryRepository {

    override fun getSearchHistory(): Flow<List<SearchHistoryItem>> {
        return searchHistoryDao.getSearchHistory().flatMapLatest { entityList ->
            flow {
                val searchHistoryItems = entityList.map { it.toSearchHistoryItem() }
                emit(searchHistoryItems)
            }
        }
    }

    override suspend fun getSearchHistoryItemByTitle(title: String): SearchHistoryItem? {
        return searchHistoryDao.getSearchHistoryItemByTitle(title)?.toSearchHistoryItem()
    }

    override suspend fun insertItem(item: SearchHistoryItem) {
        val entity = SearchHistoryEntity(title = item.title, timeMillis = item.timeMillis)
        searchHistoryDao.insert(entity)
    }

    override suspend fun updateItem(item: SearchHistoryItem) {
        val entity = SearchHistoryEntity(id = item.id, title = item.title, timeMillis = item.timeMillis)
        searchHistoryDao.update(entity)
    }

    override suspend fun deleteItem(item: SearchHistoryItem) {
        val entity = SearchHistoryEntity(
            id = item.id,
            title = item.title,
            timeMillis = item.timeMillis
        )

        searchHistoryDao.delete(entity)
    }

    override suspend fun deleteAllItems() {
        searchHistoryDao.deleteAll()
    }
}