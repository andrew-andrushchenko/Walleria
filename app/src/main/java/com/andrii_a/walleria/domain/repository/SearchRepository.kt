package com.andrii_a.walleria.domain.repository

import androidx.paging.PagingData
import com.andrii_a.walleria.core.SearchResultsContentFilter
import com.andrii_a.walleria.core.SearchResultsDisplayOrder
import com.andrii_a.walleria.core.SearchResultsPhotoColor
import com.andrii_a.walleria.core.SearchResultsPhotoOrientation
import com.andrii_a.walleria.domain.models.collection.Collection
import com.andrii_a.walleria.domain.models.photo.Photo
import com.andrii_a.walleria.domain.models.user.User
import kotlinx.coroutines.flow.Flow

interface SearchRepository {

    fun searchPhotos(
        query: String,
        order: SearchResultsDisplayOrder,
        collections: String? = null,
        contentFilter: SearchResultsContentFilter,
        color: SearchResultsPhotoColor,
        orientation: SearchResultsPhotoOrientation
    ): Flow<PagingData<Photo>>

    fun searchCollections(query: String): Flow<PagingData<Collection>>

    fun searchUsers(query: String): Flow<PagingData<User>>

}