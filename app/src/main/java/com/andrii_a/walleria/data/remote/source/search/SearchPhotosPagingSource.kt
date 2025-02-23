package com.andrii_a.walleria.data.remote.source.search

import com.andrii_a.walleria.data.remote.services.SearchService
import com.andrii_a.walleria.data.remote.source.base.BasePagingSource
import com.andrii_a.walleria.data.util.Config
import com.andrii_a.walleria.domain.SearchResultsContentFilter
import com.andrii_a.walleria.domain.SearchResultsDisplayOrder
import com.andrii_a.walleria.domain.SearchResultsPhotoColor
import com.andrii_a.walleria.domain.SearchResultsPhotoOrientation
import com.andrii_a.walleria.domain.models.photo.Photo
import com.andrii_a.walleria.domain.network.Resource
import kotlinx.coroutines.ensureActive
import kotlin.coroutines.coroutineContext

class SearchPhotosPagingSource(
    private val searchService: SearchService,
    private val query: String,
    private val order: SearchResultsDisplayOrder,
    private val collections: String?,
    private val contentFilter: SearchResultsContentFilter,
    private val color: SearchResultsPhotoColor,
    private val orientation: SearchResultsPhotoOrientation
) : BasePagingSource<Photo>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Photo> {
        val pageKey = params.key ?: Config.INITIAL_PAGE_INDEX

        if (query.isBlank()) {
            return LoadResult.Page(
                data = emptyList(),
                prevKey = null,
                nextKey = null
            )
        }

        return try {
            val result = searchService.searchPhotos(
                query = query,
                page = pageKey,
                perPage = Config.PAGE_SIZE,
                orderBy = order.value,
                collections = collections,
                contentFilter = contentFilter.value,
                color = color.value,
                orientation = orientation.value
            )

            val photos: List<Photo> = when (result) {
                is Resource.Empty, Resource.Loading -> emptyList()
                is Resource.Error -> throw result.asException()
                is Resource.Success -> result.value.results?.map { it.toPhoto() } ?: emptyList()
            }

            LoadResult.Page(
                data = photos,
                prevKey = if (pageKey == Config.INITIAL_PAGE_INDEX) null else pageKey - 1,
                nextKey = if (photos.isEmpty()) null else pageKey + 1
            )

        } catch (exception: Exception) {
            coroutineContext.ensureActive()
            LoadResult.Error(exception)
        }
    }
}