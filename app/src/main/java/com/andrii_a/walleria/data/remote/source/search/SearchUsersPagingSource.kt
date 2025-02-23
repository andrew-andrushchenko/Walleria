package com.andrii_a.walleria.data.remote.source.search

import com.andrii_a.walleria.data.remote.services.SearchService
import com.andrii_a.walleria.data.remote.source.base.BasePagingSource
import com.andrii_a.walleria.data.util.Config
import com.andrii_a.walleria.domain.models.user.User
import com.andrii_a.walleria.domain.network.Resource
import kotlinx.coroutines.ensureActive
import kotlin.coroutines.coroutineContext

class SearchUsersPagingSource(
    private val searchService: SearchService,
    private val query: String
) : BasePagingSource<User>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, User> {
        val pageKey = params.key ?: Config.INITIAL_PAGE_INDEX

        if (query.isBlank()) {
            return LoadResult.Page(
                data = emptyList(),
                prevKey = null,
                nextKey = null
            )
        }

        return try {
            val result = searchService.searchUsers(
                query = query,
                page = pageKey,
                perPage = Config.PAGE_SIZE
            )

            val users: List<User> = when (result) {
                is Resource.Empty, is Resource.Loading -> emptyList()
                is Resource.Error -> throw result.asException()
                is Resource.Success -> result.value.results?.map { it.toUser() } ?: emptyList()
            }

            LoadResult.Page(
                data = users,
                prevKey = if (pageKey == Config.INITIAL_PAGE_INDEX) null else pageKey - 1,
                nextKey = if (users.isEmpty()) null else pageKey + 1
            )

        } catch (exception: Exception) {
            coroutineContext.ensureActive()
            LoadResult.Error(exception)
        }
    }
}