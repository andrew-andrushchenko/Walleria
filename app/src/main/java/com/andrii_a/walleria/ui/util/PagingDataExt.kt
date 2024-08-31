package com.andrii_a.walleria.ui.util

import androidx.paging.LoadState
import androidx.paging.LoadStates
import androidx.paging.PagingData

fun <T : Any> emptyPagingData(): PagingData<T> {
    val loadStates = LoadStates(
        refresh = LoadState.NotLoading(endOfPaginationReached = true),
        prepend = LoadState.NotLoading(endOfPaginationReached = true),
        append = LoadState.NotLoading(endOfPaginationReached = true)
    )

    return PagingData.empty(sourceLoadStates = loadStates)
}