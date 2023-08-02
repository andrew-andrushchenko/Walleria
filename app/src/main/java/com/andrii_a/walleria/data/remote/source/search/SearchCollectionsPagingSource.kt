package com.andrii_a.walleria.data.remote.source.search

import com.andrii_a.walleria.data.remote.dto.search.SearchCollectionsResultDTO
import com.andrii_a.walleria.data.remote.service.SearchService
import com.andrii_a.walleria.data.remote.source.base.BasePagingSource
import com.andrii_a.walleria.data.util.INITIAL_PAGE_INDEX
import com.andrii_a.walleria.data.util.PAGE_SIZE
import com.andrii_a.walleria.domain.models.collection.Collection
import retrofit2.HttpException
import java.io.IOException

class SearchCollectionsPagingSource(
    private val searchService: SearchService,
    private val query: String
) : BasePagingSource<Collection>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Collection> {
        val pageKey = params.key ?: INITIAL_PAGE_INDEX

        if (query.isBlank()) {
            return LoadResult.Page(
                data = emptyList(),
                prevKey = null,
                nextKey = null
            )
        }

        return try {
            val response: SearchCollectionsResultDTO =
                searchService.searchCollections(query, pageKey, PAGE_SIZE)
            val collections: List<Collection> = response.results.map { it.toCollection() }

            LoadResult.Page(
                data = collections,
                prevKey = if (pageKey == INITIAL_PAGE_INDEX) null else pageKey - 1,
                nextKey = if (collections.isEmpty()) null else pageKey + 1
            )

        } catch (exception: IOException) {
            LoadResult.Error(exception)
        } catch (exception: HttpException) {
            LoadResult.Error(exception)
        }
    }
}