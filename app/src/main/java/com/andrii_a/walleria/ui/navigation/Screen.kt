package com.andrii_a.walleria.ui.navigation

import com.andrii_a.walleria.ui.common.CollectionId
import com.andrii_a.walleria.ui.common.PhotoId
import com.andrii_a.walleria.ui.common.SearchQuery
import com.andrii_a.walleria.ui.common.TopicId
import com.andrii_a.walleria.ui.common.UserNickname
import kotlinx.serialization.Serializable

object Screen {
    @Serializable
    data class PhotoDetails(val photoId: PhotoId)

    @Serializable
    data class CollectionDetails(val collectionId: CollectionId)

    @Serializable
    data class TopicDetails(val topicId: TopicId)

    @Serializable
    data class Search(val searchQuery: SearchQuery = "")

    @Serializable
    data class UserDetails(val userNickname: UserNickname)

    @Serializable
    data class CollectPhoto(val photoId: PhotoId)

    @Serializable
    object EditUserProfile

    //object Profile

    @Serializable
    object Settings

    @Serializable
    object About
}