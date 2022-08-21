package com.andrii_a.walleria.data.remote.source.base

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.andrii_a.walleria.data.util.INITIAL_PAGE_INDEX

abstract class BasePagingSource<Entity : Any> : PagingSource<Int, Entity>() {

    override fun getRefreshKey(state: PagingState<Int, Entity>): Int =
        state.anchorPosition?.let { position ->
            state.closestPageToPosition(position)?.prevKey
        } ?: INITIAL_PAGE_INDEX

}