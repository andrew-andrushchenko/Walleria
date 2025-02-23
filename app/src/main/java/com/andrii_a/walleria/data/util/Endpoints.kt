package com.andrii_a.walleria.data.util

object Endpoints {
    private const val BASE_API_URL = "https://api.unsplash.com"
    private const val BASE_SITE_URL = "https://unsplash.com"

    const val PHOTOS = "${BASE_API_URL}/photos"
    const val COLLECTIONS = "${BASE_API_URL}/collections"
    const val ACCESS_TOKEN = "${BASE_SITE_URL}/oauth/token"
    const val USER_PRIVATE_PROFILE = "${BASE_API_URL}/me"
    const val SEARCH_PHOTOS = "${BASE_API_URL}/search/photos"
    const val SEARCH_COLLECTIONS = "${BASE_API_URL}/search/collections"
    const val SEARCH_USERS = "${BASE_API_URL}/search/users"
    const val RANDOM_PHOTOS = "${BASE_API_URL}/photos/random"
    const val TOPICS = "${BASE_API_URL}/topics"

    fun collectionPhotos(id: String) = "${BASE_API_URL}/collections/$id/photos"

    fun userPhotos(username: String) = "${BASE_API_URL}/users/$username/photos"

    fun userLikedPhotos(username: String) = "${BASE_API_URL}/users/$username/likes"

    fun topicPhotos(idOrSlug: String) = "${BASE_API_URL}/topics/$idOrSlug/photos"

    fun singlePhoto(id: String) = "${BASE_API_URL}/photos/$id"

    fun likeDislikePhoto(id: String) = "${BASE_API_URL}/photos/$id/like"

    fun trackPhotoDownload(photoId: String) = "${BASE_API_URL}/photos/$photoId/download"

    fun singleCollection(id: String) = "${BASE_API_URL}/collections/$id"

    fun userCollections(username: String) = "${BASE_API_URL}/users/$username/collections"

    fun addToCollection(collectionId: String) = "${BASE_API_URL}/collections/$collectionId/add"

    fun deleteFromCollection(collectionId: String) = "${BASE_API_URL}/collections/$collectionId/remove"

    fun relatedCollections(id: String) = "${BASE_API_URL}/collections/$id/related"

    fun singleTopic(idOrSlug: String) = "${BASE_API_URL}/topics/$idOrSlug"

    fun singleUser(username: String) = "${BASE_API_URL}/users/$username"
}