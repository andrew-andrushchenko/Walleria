package com.andrii_a.walleria.ui.search

import com.andrii_a.walleria.ui.common.CollectionId
import com.andrii_a.walleria.ui.common.PhotoId
import com.andrii_a.walleria.ui.common.UserNickname

sealed interface SearchNavigationEvent {
    data object NavigateBack : SearchNavigationEvent
    data class NavigateToPhotoDetails(val photoId: PhotoId) : SearchNavigationEvent
    data class NavigateToCollectionDetails(val collectionId: CollectionId) : SearchNavigationEvent
    data class NavigateToUserDetails(val userNickname: UserNickname) : SearchNavigationEvent

}