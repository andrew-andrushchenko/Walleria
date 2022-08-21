package com.andrii_a.walleria.domain.repository

import androidx.paging.PagingData
import com.andrii_a.walleria.core.SearchContentFilter
import com.andrii_a.walleria.core.SearchOrder
import com.andrii_a.walleria.core.SearchPhotoColor
import com.andrii_a.walleria.core.SearchPhotoOrientation
import com.andrii_a.walleria.domain.models.collection.Collection
import com.andrii_a.walleria.domain.models.photo.Photo
import com.andrii_a.walleria.domain.models.user.User
import kotlinx.coroutines.flow.Flow

interface SearchRepository {

    fun searchPhotos(
        query: String,
        order: SearchOrder,
        collections: String? = null,
        contentFilter: SearchContentFilter,
        color: SearchPhotoColor,
        orientation: SearchPhotoOrientation
    ): Flow<PagingData<Photo>>

    fun searchCollections(query: String): Flow<PagingData<Collection>>

    fun searchUsers(query: String): Flow<PagingData<User>>

}