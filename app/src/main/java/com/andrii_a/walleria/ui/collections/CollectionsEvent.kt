package com.andrii_a.walleria.ui.collections

import com.andrii_a.walleria.ui.common.CollectionId
import com.andrii_a.walleria.ui.common.PhotoId
import com.andrii_a.walleria.ui.common.UserNickname

sealed interface CollectionsEvent {
    data object GetCollections : CollectionsEvent
    data class SelectCollection(val collectionId: CollectionId) : CollectionsEvent
    data class SelectPhoto(val photoId: PhotoId) : CollectionsEvent
    data class SelectUser(val userNickname: UserNickname) : CollectionsEvent
    data object SelectSearch : CollectionsEvent
}
