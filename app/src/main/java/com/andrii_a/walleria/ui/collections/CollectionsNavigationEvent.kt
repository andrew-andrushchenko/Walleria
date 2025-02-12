package com.andrii_a.walleria.ui.collections

import com.andrii_a.walleria.ui.common.CollectionId
import com.andrii_a.walleria.ui.common.PhotoId
import com.andrii_a.walleria.ui.common.UserNickname


sealed interface CollectionsNavigationEvent {
    data object NavigateToSearchScreen : CollectionsNavigationEvent
    data class NavigateToCollectionDetails(val collectionId: CollectionId) : CollectionsNavigationEvent
    data class NavigateToPhotoDetailsScreen(val photoId: PhotoId) : CollectionsNavigationEvent
    data class NavigateToUserDetails(val userNickname: UserNickname) : CollectionsNavigationEvent
}