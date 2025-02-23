package com.andrii_a.walleria.data.remote.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.andrii_a.walleria.data.remote.services.SearchService
import com.andrii_a.walleria.data.remote.source.search.SearchCollectionsPagingSource
import com.andrii_a.walleria.data.remote.source.search.SearchPhotosPagingSource
import com.andrii_a.walleria.data.remote.source.search.SearchUsersPagingSource
import com.andrii_a.walleria.data.util.Config
import com.andrii_a.walleria.domain.SearchResultsContentFilter
import com.andrii_a.walleria.domain.SearchResultsDisplayOrder
import com.andrii_a.walleria.domain.SearchResultsPhotoColor
import com.andrii_a.walleria.domain.SearchResultsPhotoOrientation
import com.andrii_a.walleria.domain.models.collection.Collection
import com.andrii_a.walleria.domain.models.photo.Photo
import com.andrii_a.walleria.domain.models.user.User
import com.andrii_a.walleria.domain.repository.SearchRepository
import kotlinx.coroutines.flow.Flow

class SearchRepositoryImpl(private val searchService: SearchService) : SearchRepository {

    override fun searchPhotos(
        query: String,
        order: SearchResultsDisplayOrder,
        collections: String?,
        contentFilter: SearchResultsContentFilter,
        color: SearchResultsPhotoColor,
        orientation: SearchResultsPhotoOrientation
    ): Flow<PagingData<Photo>> =
        Pager(
            config = PagingConfig(
                pageSize = Config.PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                SearchPhotosPagingSource(
                    searchService = searchService,
                    query = query,
                    order = order,
                    collections = collections,
                    contentFilter = contentFilter,
                    color = color,
                    orientation = orientation
                )
            }
        ).flow

    override fun searchCollections(query: String): Flow<PagingData<Collection>> =
        Pager(
            config = PagingConfig(
                pageSize = Config.PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { SearchCollectionsPagingSource(searchService, query) }
        ).flow

    override fun searchUsers(query: String): Flow<PagingData<User>> =
        Pager(
            config = PagingConfig(
                pageSize = Config.PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { SearchUsersPagingSource(searchService, query) }
        ).flow
}