package com.andrii_a.walleria.data.remote.services

import com.andrii_a.walleria.data.remote.dto.collect_photo.CollectionPhotoResultDto
import com.andrii_a.walleria.data.remote.dto.collection.CollectionDto
import com.andrii_a.walleria.data.util.Endpoints
import com.andrii_a.walleria.data.util.backendRequest
import com.andrii_a.walleria.domain.network.Resource
import io.ktor.client.HttpClient
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put

class CollectionsServiceImpl(private val client: HttpClient) : CollectionsService {

    override suspend fun getCollections(page: Int?, perPage: Int?): Resource<List<CollectionDto>> {
        return backendRequest {
            client.get(urlString = Endpoints.COLLECTIONS) {
                url {
                    parameters.append("page", page.toString())
                    parameters.append("per_page", perPage.toString())
                }
            }
        }
    }

    override suspend fun getCollection(id: String): Resource<CollectionDto> {
        return backendRequest {
            client.get(urlString = Endpoints.singleCollection(id))
        }
    }

    override suspend fun getUserCollections(
        username: String,
        page: Int?,
        perPage: Int?
    ): Resource<List<CollectionDto>> {
        return backendRequest {
            client.get(urlString = Endpoints.userCollections(username)) {
                url {
                    parameters.append("page", page.toString())
                    parameters.append("per_page", perPage.toString())
                }
            }
        }
    }

    override suspend fun createCollection(
        title: String,
        description: String?,
        isPrivate: Boolean?
    ): Resource<CollectionDto> {
        return backendRequest {
            client.post(urlString = Endpoints.COLLECTIONS) {
                url {
                    parameters.append("title", title)
                    parameters.append("description", description.toString())
                    parameters.append("private", isPrivate.toString())
                }
            }
        }
    }

    override suspend fun updateCollection(
        id: String,
        title: String?,
        description: String?,
        isPrivate: Boolean?
    ): Resource<CollectionDto> {
        return backendRequest {
            client.put(urlString = Endpoints.singleCollection(id)) {
                url {
                    parameters.append("title", title.toString())
                    parameters.append("description", description.toString())
                    parameters.append("private", isPrivate.toString())
                }
            }
        }
    }

    override suspend fun deleteCollection(id: String): Resource<Unit> {
        return backendRequest {
            client.delete(urlString = Endpoints.singleCollection(id))
        }
    }

    override suspend fun addPhotoToCollection(
        collectionId: String,
        photoId: String
    ): Resource<CollectionPhotoResultDto> {
        return backendRequest {
            client.post(urlString = Endpoints.addToCollection(collectionId)) {
                url {
                    parameters.append("photo_id", photoId)
                }
            }
        }
    }

    override suspend fun deletePhotoFromCollection(
        collectionId: String,
        photoId: String
    ): Resource<CollectionPhotoResultDto> {
        return backendRequest {
            client.delete(urlString = Endpoints.deleteFromCollection(collectionId)) {
                url {
                    parameters.append("photo_id", photoId)
                }
            }
        }
    }

    override suspend fun getRelatedCollections(id: String): Resource<List<CollectionDto>> {
        return backendRequest {
            client.get(urlString = Endpoints.relatedCollections(id))
        }
    }
}