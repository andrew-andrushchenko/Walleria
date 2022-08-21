package com.andrii_a.walleria.data.remote.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.andrii_a.walleria.core.SearchContentFilter
import com.andrii_a.walleria.core.SearchOrder
import com.andrii_a.walleria.core.SearchPhotoColor
import com.andrii_a.walleria.core.SearchPhotoOrientation
import com.andrii_a.walleria.data.remote.source.search.SearchCollectionsPagingSource
import com.andrii_a.walleria.data.remote.source.search.SearchPhotosPagingSource
import com.andrii_a.walleria.data.remote.source.search.SearchService
import com.andrii_a.walleria.data.remote.source.search.SearchUsersPagingSource
import com.andrii_a.walleria.data.util.PAGE_SIZE
import com.andrii_a.walleria.domain.models.collection.Collection
import com.andrii_a.walleria.domain.models.photo.Photo
import com.andrii_a.walleria.domain.models.user.User
import com.andrii_a.walleria.domain.repository.SearchRepository
import kotlinx.coroutines.flow.Flow

class SearchRepositoryImpl(private val searchService: SearchService) : SearchRepository {

    override fun searchPhotos(
        query: String,
        order: SearchOrder,
        collections: String?,
        contentFilter: SearchContentFilter,
        color: SearchPhotoColor,
        orientation: SearchPhotoOrientation
    ): Flow<PagingData<Photo>> =
        Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                SearchPhotosPagingSource(
                    searchService,
                    query,
                    order,
                    collections,
                    contentFilter,
                    color,
                    orientation
                )
            }
        ).flow

    override fun searchCollections(query: String): Flow<PagingData<Collection>> =
        Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { SearchCollectionsPagingSource(searchService, query) }
        ).flow

    override fun searchUsers(query: String): Flow<PagingData<User>> =
        Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { SearchUsersPagingSource(searchService, query) }
        ).flow
}