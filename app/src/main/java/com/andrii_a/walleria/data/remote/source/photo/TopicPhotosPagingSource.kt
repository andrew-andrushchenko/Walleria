package com.andrii_a.walleria.data.remote.source.photo

import com.andrii_a.walleria.data.remote.services.PhotoService
import com.andrii_a.walleria.data.remote.source.base.BasePagingSource
import com.andrii_a.walleria.data.util.Config
import com.andrii_a.walleria.domain.PhotoListDisplayOrder
import com.andrii_a.walleria.domain.TopicPhotosOrientation
import com.andrii_a.walleria.domain.models.photo.Photo
import com.andrii_a.walleria.domain.network.Resource
import kotlinx.coroutines.ensureActive
import kotlin.coroutines.coroutineContext

class TopicPhotosPagingSource(
    private val photoService: PhotoService,
    private val idOrSlug: String,
    private val orientation: TopicPhotosOrientation,
    private val order: PhotoListDisplayOrder
) : BasePagingSource<Photo>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Photo> {
        val pageKey = params.key ?: Config.INITIAL_PAGE_INDEX

        return try {
            val result = photoService.getTopicPhotos(
                idOrSlug = idOrSlug,
                page = pageKey,
                perPage = Config.PAGE_SIZE,
                orientation = orientation.value,
                orderBy = order.value
            )

            val topicPhotos: List<Photo> = when (result) {
                is Resource.Empty, Resource.Loading -> emptyList()
                is Resource.Error -> throw result.asException()
                is Resource.Success -> result.value.map { it.toPhoto() }
            }

            LoadResult.Page(
                data = topicPhotos,
                prevKey = if (pageKey == Config.INITIAL_PAGE_INDEX) null else pageKey - 1,
                nextKey = if (topicPhotos.isEmpty()) null else pageKey + 1
            )
        } catch (exception: Exception) {
            coroutineContext.ensureActive()
            LoadResult.Error(exception)
        }
    }

}