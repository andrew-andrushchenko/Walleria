package com.andrii_a.walleria.data.remote.source.collection

import com.andrii_a.walleria.data.remote.service.CollectionsService
import retrofit2.HttpException
import com.andrii_a.walleria.data.remote.source.base.BasePagingSource
import com.andrii_a.walleria.data.util.INITIAL_PAGE_INDEX
import com.andrii_a.walleria.data.util.PAGE_SIZE
import com.andrii_a.walleria.domain.models.collection.Collection
import java.io.IOException

class UserCollectionsPagingSource(
    private val collectionsService: CollectionsService,
    private val username: String
) : BasePagingSource<Collection>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Collection> {
        val pageKey = params.key ?: INITIAL_PAGE_INDEX

        return try {
            val userCollections: List<Collection> = collectionsService.getUserCollections(
                username,
                pageKey,
                PAGE_SIZE
            ).map { it.toCollection() }

            LoadResult.Page(
                data = userCollections,
                prevKey = if (pageKey == INITIAL_PAGE_INDEX) null else pageKey - 1,
                nextKey = if (userCollections.isEmpty()) null else pageKey + 1
            )
        } catch (exception: IOException) {
            LoadResult.Error(exception)
        } catch (exception: HttpException) {
            LoadResult.Error(exception)
        }
    }
}