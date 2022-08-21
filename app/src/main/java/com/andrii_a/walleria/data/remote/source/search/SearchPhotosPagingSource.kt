package com.andrii_a.walleria.data.remote.source.search

import com.andrii_a.walleria.core.SearchContentFilter
import com.andrii_a.walleria.core.SearchOrder
import com.andrii_a.walleria.core.SearchPhotoColor
import com.andrii_a.walleria.core.SearchPhotoOrientation
import com.andrii_a.walleria.data.remote.dto.search.SearchPhotosResultDTO
import com.andrii_a.walleria.data.remote.source.base.BasePagingSource
import com.andrii_a.walleria.data.util.INITIAL_PAGE_INDEX
import com.andrii_a.walleria.data.util.PAGE_SIZE
import com.andrii_a.walleria.domain.models.photo.Photo
import retrofit2.HttpException
import java.io.IOException

class SearchPhotosPagingSource(
    private val searchService: SearchService,
    private val query: String,
    private val order: SearchOrder,
    private val collections: String?,
    private val contentFilter: SearchContentFilter,
    private val color: SearchPhotoColor,
    private val orientation: SearchPhotoOrientation
) : BasePagingSource<Photo>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Photo> {
        val pageKey = params.key ?: INITIAL_PAGE_INDEX

        return try {
            val response: SearchPhotosResultDTO = searchService.searchPhotos(
                query = query,
                page = pageKey,
                perPage = PAGE_SIZE,
                orderBy = order.value,
                collections = collections,
                contentFilter = contentFilter.value,
                color = color.value,
                orientation = orientation.value
            )

            val photos: List<Photo> = response.results.map { it.toPhoto() }

            LoadResult.Page(
                data = photos,
                prevKey = if (pageKey == INITIAL_PAGE_INDEX) null else pageKey - 1,
                nextKey = if (photos.isEmpty()) null else pageKey + 1
            )

        } catch (exception: IOException) {
            LoadResult.Error(exception)
        } catch (exception: HttpException) {
            LoadResult.Error(exception)
        }
    }
}