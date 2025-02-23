package com.andrii_a.walleria.data.remote.services

import com.andrii_a.walleria.data.remote.dto.search.SearchCollectionsResultDto
import com.andrii_a.walleria.data.remote.dto.search.SearchPhotosResultDto
import com.andrii_a.walleria.data.remote.dto.search.SearchUsersResultDto
import com.andrii_a.walleria.domain.network.Resource

interface SearchService {

    suspend fun searchPhotos(
        query: String,
        page: Int?,
        perPage: Int?,
        orderBy: String?,
        collections: String?,
        contentFilter: String?,
        color: String?,
        orientation: String?
    ): Resource<SearchPhotosResultDto>

    suspend fun searchCollections(
        query: String,
        page: Int?,
        perPage: Int?
    ): Resource<SearchCollectionsResultDto>

    suspend fun searchUsers(
        query: String,
        page: Int?,
        perPage: Int?
    ): Resource<SearchUsersResultDto>
}