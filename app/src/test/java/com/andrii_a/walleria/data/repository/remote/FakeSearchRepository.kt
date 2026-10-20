package com.andrii_a.walleria.data.repository.remote

import androidx.paging.PagingData
import com.andrii_a.walleria.domain.SearchResultsContentFilter
import com.andrii_a.walleria.domain.SearchResultsDisplayOrder
import com.andrii_a.walleria.domain.SearchResultsPhotoColor
import com.andrii_a.walleria.domain.SearchResultsPhotoOrientation
import com.andrii_a.walleria.domain.models.collection.Collection
import com.andrii_a.walleria.domain.models.photo.Photo
import com.andrii_a.walleria.domain.models.user.User
import com.andrii_a.walleria.domain.repository.SearchRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeSearchRepository : SearchRepository {

    var lastSearchedQuery: String? = null
    var lastOrder: SearchResultsDisplayOrder? = null
    var lastCollections: String? = null
    var lastContentFilter: SearchResultsContentFilter? = null
    var lastColor: SearchResultsPhotoColor? = null
    var lastOrientation: SearchResultsPhotoOrientation? = null

    override fun searchPhotos(
        query: String,
        order: SearchResultsDisplayOrder,
        collections: String?,
        contentFilter: SearchResultsContentFilter,
        color: SearchResultsPhotoColor,
        orientation: SearchResultsPhotoOrientation
    ): Flow<PagingData<Photo>> {
        lastSearchedQuery = query
        lastOrder = order
        lastCollections = collections
        lastContentFilter = contentFilter
        lastColor = color
        lastOrientation = orientation
        return flowOf(PagingData.empty())
    }

    override fun searchCollections(query: String): Flow<PagingData<Collection>> {
        lastSearchedQuery = query
        return flowOf(PagingData.empty())
    }

    override fun searchUsers(query: String): Flow<PagingData<User>> {
        lastSearchedQuery = query
        return flowOf(PagingData.empty())
    }
}