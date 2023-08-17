package com.andrii_a.walleria.data.local.repository

import com.andrii_a.walleria.data.local.db.dao.RecentSearchesDao
import com.andrii_a.walleria.data.local.db.entities.RecentSearchItemEntity
import com.andrii_a.walleria.domain.models.search.RecentSearchItem
import com.andrii_a.walleria.domain.repository.RecentSearchesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow

class RecentSearchesRepositoryImpl(
    private val recentSearchesDao: RecentSearchesDao
) : RecentSearchesRepository {

    override fun getAllRecentSearches(): Flow<List<RecentSearchItem>> {
        return recentSearchesDao.getRecentSearches().flatMapLatest { entityList ->
            flow {
                val recentSearchItems = entityList.map { it.toRecentSearchItem() }
                emit(recentSearchItems)
            }
        }
    }

    override suspend fun insertItem(item: RecentSearchItem) {
        val entity = RecentSearchItemEntity(title = item.title, timeMillis = item.timeMillis)
        recentSearchesDao.insert(entity)
    }

    override suspend fun updateItem(item: RecentSearchItem) {
        val entity = RecentSearchItemEntity(id = item.id, title = item.title, timeMillis = item.timeMillis)
        recentSearchesDao.update(entity)
    }

    override suspend fun deleteItem(item: RecentSearchItem) {
        val entity = RecentSearchItemEntity(
            id = item.id,
            title = item.title,
            timeMillis = item.timeMillis
        )

        recentSearchesDao.delete(entity)
    }

    override suspend fun deleteAllItems() {
        recentSearchesDao.deleteAll()
    }
}