package com.andrii_a.walleria.data.remote.source.photo

import com.andrii_a.walleria.data.remote.services.PhotoService
import com.andrii_a.walleria.data.remote.source.base.BasePagingSource
import com.andrii_a.walleria.data.util.Config
import com.andrii_a.walleria.domain.models.photo.Photo
import com.andrii_a.walleria.domain.network.Resource
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive

class UserPhotosPagingSource(
    private val photoService: PhotoService,
    private val username: String
) : BasePagingSource<Photo>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Photo> {
        val pageKey = params.key ?: Config.INITIAL_PAGE_INDEX

        return try {
            val result = photoService.getUserPhotos(
                username = username,
                page = pageKey,
                perPage = Config.PAGE_SIZE,
                orderBy = "latest",
                stats = false,
                resolution = null,
                quantity = null,
                orientation = null
            )

            val userPhotos: List<Photo> = when (result) {
                is Resource.Empty, Resource.Loading -> emptyList()
                is Resource.Error -> throw result.asException()
                is Resource.Success -> result.value.map { it.toPhoto() }
            }

            LoadResult.Page(
                data = userPhotos,
                prevKey = if (pageKey == Config.INITIAL_PAGE_INDEX) null else pageKey - 1,
                nextKey = if (userPhotos.isEmpty()) null else pageKey + 1
            )
        } catch (exception: Exception) {
            currentCoroutineContext().ensureActive()
            LoadResult.Error(exception)
        }
    }
}