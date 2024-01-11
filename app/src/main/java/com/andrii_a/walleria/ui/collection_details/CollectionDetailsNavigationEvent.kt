package com.andrii_a.walleria.ui.collection_details

import com.andrii_a.walleria.ui.common.PhotoId
import com.andrii_a.walleria.ui.common.UserNickname

sealed interface CollectionDetailsNavigationEvent {
    data object NavigateBack : CollectionDetailsNavigationEvent
    data class NavigateToUserDetails(val userNickname: UserNickname) : CollectionDetailsNavigationEvent
    data class NavigateToPhotoDetails(val photoId: PhotoId) : CollectionDetailsNavigationEvent
}