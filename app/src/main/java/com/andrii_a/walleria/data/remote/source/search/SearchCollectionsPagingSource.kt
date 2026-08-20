package com.andrii_a.walleria.data.remote.source.search

import com.andrii_a.walleria.data.remote.services.SearchService
import com.andrii_a.walleria.data.remote.source.base.BasePagingSource
import com.andrii_a.walleria.data.util.Config
import com.andrii_a.walleria.domain.models.collection.Collection
import com.andrii_a.walleria.domain.network.Resource
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive

class SearchCollectionsPagingSource(
    private val searchService: SearchService,
    private val query: String
) : BasePagingSource<Collection>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Collection> {
        val pageKey = params.key ?: Config.INITIAL_PAGE_INDEX

        if (query.isBlank()) {
            return LoadResult.Page(
                data = emptyList(),
                prevKey = null,
                nextKey = null
            )
        }

        return try {
            val result = searchService.searchCollections(
                query = query,
                page = pageKey,
                perPage = Config.PAGE_SIZE
            )

            val collections: List<Collection> = when (result) {
                is Resource.Empty, is Resource.Loading -> emptyList()
                is Resource.Error -> throw result.asException()
                is Resource.Success -> result.value.results?.map { it.toCollection() }
                    ?: emptyList()
            }

            LoadResult.Page(
                data = collections,
                prevKey = if (pageKey == Config.INITIAL_PAGE_INDEX) null else pageKey - 1,
                nextKey = if (collections.isEmpty()) null else pageKey + 1
            )

        } catch (exception: Exception) {
            currentCoroutineContext().ensureActive()
            LoadResult.Error(exception)
        }
    }
}