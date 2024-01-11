package com.andrii_a.walleria.ui.collection_details

import com.andrii_a.walleria.ui.common.CollectionId
import com.andrii_a.walleria.ui.common.PhotoId
import com.andrii_a.walleria.ui.common.UserNickname

sealed interface CollectionDetailsEvent {
    data class RequestCollection(val collectionId: CollectionId) : CollectionDetailsEvent

    data class UpdateCollection(
        val collectionId: CollectionId,
        val title: String,
        val description: String?,
        val isPrivate: Boolean
    ) : CollectionDetailsEvent

    data class DeleteCollection(val collectionId: CollectionId) : CollectionDetailsEvent
    data object GoBack : CollectionDetailsEvent
    data class SelectUser(val userNickname: UserNickname) : CollectionDetailsEvent
    data class SelectPhoto(val photoId: PhotoId) : CollectionDetailsEvent
}