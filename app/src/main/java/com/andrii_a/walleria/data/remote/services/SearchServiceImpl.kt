package com.andrii_a.walleria.data.remote.services

import com.andrii_a.walleria.data.remote.dto.search.SearchCollectionsResultDto
import com.andrii_a.walleria.data.remote.dto.search.SearchPhotosResultDto
import com.andrii_a.walleria.data.remote.dto.search.SearchUsersResultDto
import com.andrii_a.walleria.data.util.Endpoints
import com.andrii_a.walleria.data.util.backendRequest
import com.andrii_a.walleria.domain.network.Resource
import io.ktor.client.HttpClient
import io.ktor.client.request.get

class SearchServiceImpl(private val httpClient: HttpClient) : SearchService {

    override suspend fun searchPhotos(
        query: String,
        page: Int?,
        perPage: Int?,
        orderBy: String?,
        collections: String?,
        contentFilter: String?,
        color: String?,
        orientation: String?
    ): Resource<SearchPhotosResultDto> {
        return backendRequest {
            httpClient.get(Endpoints.SEARCH_PHOTOS) {
                url {
                    parameters.append("query", query)
                    parameters.append("page", page.toString())
                    parameters.append("per_page", perPage.toString())
                    orderBy?.let {
                        parameters.append("order_by", orderBy)
                    }
                    collections?.let {
                        parameters.append("collections", collections)
                    }
                    contentFilter?.let {
                        parameters.append("content_filter", contentFilter)
                    }
                    color?.let {
                        parameters.append("color", color)
                    }
                    orientation?.let {
                        parameters.append("orientation", orientation)
                    }
                }
            }
        }
    }

    override suspend fun searchCollections(
        query: String,
        page: Int?,
        perPage: Int?
    ): Resource<SearchCollectionsResultDto> {
        return backendRequest {
            httpClient.get(Endpoints.SEARCH_COLLECTIONS) {
                url {
                    parameters.append("query", query)
                    parameters.append("page", page.toString())
                    parameters.append("per_page", perPage.toString())
                }
            }
        }
    }

    override suspend fun searchUsers(
        query: String,
        page: Int?,
        perPage: Int?
    ): Resource<SearchUsersResultDto> {
        return backendRequest {
            httpClient.get(Endpoints.SEARCH_USERS) {
                url {
                    parameters.append("query", query)
                    parameters.append("page", page.toString())
                    parameters.append("per_page", perPage.toString())
                }
            }
        }
    }
}