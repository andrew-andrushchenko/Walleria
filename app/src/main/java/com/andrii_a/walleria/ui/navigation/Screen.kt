package com.andrii_a.walleria.ui.navigation

import com.andrii_a.walleria.ui.common.CollectionId
import com.andrii_a.walleria.ui.common.PhotoId
import com.andrii_a.walleria.ui.common.SearchQuery
import com.andrii_a.walleria.ui.common.TopicId
import com.andrii_a.walleria.ui.common.UserNickname
import kotlinx.serialization.Serializable

sealed interface Screen {
    @Serializable
    data object Photos : Screen

    @Serializable
    data object Collections : Screen

    @Serializable
    data object Topics : Screen

    @Serializable
    data class PhotoDetails(val photoId: PhotoId) : Screen

    @Serializable
    data class CollectionDetails(val collectionId: CollectionId) : Screen

    @Serializable
    data class TopicDetails(val topicId: TopicId) : Screen

    @Serializable
    data class Search(val searchQuery: SearchQuery = "") : Screen

    @Serializable
    data class UserDetails(val userNickname: UserNickname) : Screen

    @Serializable
    data object Login : Screen

    @Serializable
    data class CollectPhoto(val photoId: PhotoId) : Screen

    @Serializable
    data object EditUserProfile : Screen

    @Serializable
    data object AccountAndSettings : Screen

    @Serializable
    data object Settings : Screen

    @Serializable
    data object About : Screen
}