package com.andrii_a.walleria.data.remote.source.collection

import com.andrii_a.walleria.data.remote.services.CollectionsService
import com.andrii_a.walleria.data.remote.source.base.BasePagingSource
import com.andrii_a.walleria.data.util.Config
import com.andrii_a.walleria.domain.models.collection.Collection
import com.andrii_a.walleria.domain.network.Resource
import kotlinx.coroutines.ensureActive
import kotlin.coroutines.coroutineContext

class CollectionsPagingSource(
    private val collectionsService: CollectionsService
) : BasePagingSource<Collection>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Collection> {
        val pageKey = params.key ?: Config.INITIAL_PAGE_INDEX

        return try {
            val result = collectionsService.getCollections(
                page = pageKey,
                perPage = Config.PAGE_SIZE
            )

            val collections: List<Collection> = when (result) {
                is Resource.Empty, Resource.Loading -> emptyList()
                is Resource.Error -> throw result.asException()
                is Resource.Success -> result.value.map { it.toCollection() }
            }

            LoadResult.Page(
                data = collections,
                prevKey = if (pageKey == Config.INITIAL_PAGE_INDEX) null else pageKey - 1,
                nextKey = if (collections.isEmpty()) null else pageKey + 1
            )

        } catch (exception: Exception) {
            coroutineContext.ensureActive()
            LoadResult.Error(exception)
        }
    }
}