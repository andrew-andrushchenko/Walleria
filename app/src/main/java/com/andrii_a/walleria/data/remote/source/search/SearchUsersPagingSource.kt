package com.andrii_a.walleria.data.remote.source.search

import com.andrii_a.walleria.data.remote.dto.search.SearchUsersResultDTO
import com.andrii_a.walleria.data.remote.source.base.BasePagingSource
import com.andrii_a.walleria.data.util.INITIAL_PAGE_INDEX
import com.andrii_a.walleria.data.util.PAGE_SIZE
import com.andrii_a.walleria.domain.models.user.User
import retrofit2.HttpException
import java.io.IOException

class SearchUsersPagingSource(
    private val searchService: SearchService,
    private val query: String
) : BasePagingSource<User>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, User> {
        val pageKey = params.key ?: INITIAL_PAGE_INDEX

        if (query.isBlank()) {
            return LoadResult.Page(
                data = emptyList(),
                prevKey = null,
                nextKey = null
            )
        }

        return try {
            val response: SearchUsersResultDTO =
                searchService.searchUsers(query, pageKey, PAGE_SIZE)
            val users: List<User> = response.results.map { it.toUser() }

            LoadResult.Page(
                data = users,
                prevKey = if (pageKey == INITIAL_PAGE_INDEX) null else pageKey - 1,
                nextKey = if (users.isEmpty()) null else pageKey + 1
            )

        } catch (exception: IOException) {
            LoadResult.Error(exception)
        } catch (exception: HttpException) {
            LoadResult.Error(exception)
        }
    }
}