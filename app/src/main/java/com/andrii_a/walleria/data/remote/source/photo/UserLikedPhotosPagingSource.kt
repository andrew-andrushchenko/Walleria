package com.andrii_a.walleria.data.remote.source.photo

import com.andrii_a.walleria.data.remote.service.PhotoService
import com.andrii_a.walleria.data.remote.source.base.BasePagingSource
import com.andrii_a.walleria.data.util.INITIAL_PAGE_INDEX
import com.andrii_a.walleria.data.util.PAGE_SIZE
import com.andrii_a.walleria.domain.models.photo.Photo
import retrofit2.HttpException
import java.io.IOException

class UserLikedPhotosPagingSource(
    private val photoService: PhotoService,
    private val username: String
) : BasePagingSource<Photo>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Photo> {
        val pageKey = params.key ?: INITIAL_PAGE_INDEX

        return try {
            val likedPhotos: List<Photo> =
                photoService.getUserLikedPhotos(
                    username,
                    pageKey,
                    PAGE_SIZE,
                    "latest",
                    null
                ).map { it.toPhoto() }

            LoadResult.Page(
                data = likedPhotos,
                prevKey = if (pageKey == INITIAL_PAGE_INDEX) null else pageKey - 1,
                nextKey = if (likedPhotos.isEmpty()) null else pageKey + 1
            )
        } catch (exception: IOException) {
            LoadResult.Error(exception)
        } catch (exception: HttpException) {
            LoadResult.Error(exception)
        }
    }
}