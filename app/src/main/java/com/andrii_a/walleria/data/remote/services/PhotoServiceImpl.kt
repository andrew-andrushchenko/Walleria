package com.andrii_a.walleria.data.remote.services

import com.andrii_a.walleria.data.remote.dto.photo.PhotoDto
import com.andrii_a.walleria.data.util.Endpoints
import com.andrii_a.walleria.data.util.backendRequest
import com.andrii_a.walleria.domain.network.Resource
import io.ktor.client.HttpClient
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post

class PhotoServiceImpl(private val httpClient: HttpClient) : PhotoService {

    override suspend fun getPhotos(
        page: Int?,
        perPage: Int?,
        orderBy: String?
    ): Resource<List<PhotoDto>> {
        return backendRequest {
            httpClient.get(urlString = Endpoints.PHOTOS) {
                url {
                    parameters.append("page", page.toString())
                    parameters.append("per_page", perPage.toString())
                    parameters.append("order_by", orderBy.toString())
                }
            }
        }
    }

    override suspend fun getCollectionPhotos(
        id: String,
        page: Int?,
        perPage: Int?
    ): Resource<List<PhotoDto>> {
        return backendRequest {
            httpClient.get(urlString = Endpoints.collectionPhotos(id)) {
                url {
                    parameters.append("page", page.toString())
                    parameters.append("per_page", perPage.toString())
                }
            }
        }
    }

    override suspend fun getUserPhotos(
        username: String,
        page: Int?,
        perPage: Int?,
        orderBy: String?,
        stats: Boolean?,
        resolution: String?,
        quantity: Int?,
        orientation: String?
    ): Resource<List<PhotoDto>> {
        return backendRequest {
            httpClient.get(urlString = Endpoints.userPhotos(username)) {
                url {
                    parameters.append("page", page.toString())
                    parameters.append("per_page", perPage.toString())
                    orderBy?.let {
                        parameters.append("order_by", orderBy.toString())
                    }
                    stats?.let {
                        parameters.append("stats", stats.toString())
                    }
                    resolution?.let {
                        parameters.append("resolution", resolution.toString())
                    }
                    quantity?.let {
                        parameters.append("quantity", quantity.toString())
                    }
                    orientation?.let {
                        parameters.append("orientation", orientation.toString())
                    }
                }
            }
        }
    }

    override suspend fun getUserLikedPhotos(
        username: String,
        page: Int?,
        perPage: Int?,
        orderBy: String?,
        orientation: String?
    ): Resource<List<PhotoDto>> {
        return backendRequest {
            httpClient.get(
                urlString = Endpoints.userLikedPhotos(
                    username
                )
            ) {
                url {
                    parameters.append("page", page.toString())
                    parameters.append("per_page", perPage.toString())
                    orderBy?.let {
                        parameters.append("order_by", orderBy.toString())
                    }
                    orientation?.let {
                        parameters.append("orientation", orientation.toString())
                    }
                }
            }
        }
    }

    override suspend fun getTopicPhotos(
        idOrSlug: String,
        page: Int?,
        perPage: Int?,
        orientation: String?,
        orderBy: String?
    ): Resource<List<PhotoDto>> {
        return backendRequest {
            httpClient.get(urlString = Endpoints.topicPhotos(idOrSlug)) {
                url {
                    parameters.append("page", page.toString())
                    parameters.append("per_page", perPage.toString())
                    orderBy?.let {
                        parameters.append("order_by", orderBy.toString())
                    }
                    orientation?.let {
                        parameters.append("orientation", orientation.toString())
                    }
                }
            }
        }
    }

    override suspend fun getPhoto(id: String): Resource<PhotoDto> {
        return backendRequest {
            httpClient.get(urlString = Endpoints.singlePhoto(id))
        }
    }

    override suspend fun likePhoto(id: String): Resource<Unit> {
        return backendRequest {
            httpClient.post(urlString = Endpoints.likeDislikePhoto(id))
        }
    }

    override suspend fun dislikePhoto(id: String): Resource<Unit> {
        return backendRequest {
            httpClient.delete(urlString = Endpoints.likeDislikePhoto(id))
        }
    }

    override suspend fun trackDownload(photoId: String): Resource<Unit> {
        return backendRequest {
            httpClient.get(
                urlString = Endpoints.trackPhotoDownload(photoId)
            )
        }
    }
}