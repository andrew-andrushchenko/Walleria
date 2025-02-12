package com.andrii_a.walleria.ui.photos

import com.andrii_a.walleria.ui.common.PhotoId
import com.andrii_a.walleria.ui.common.UserNickname

sealed interface PhotosNavigationEvent {
    data object NavigateToSearchScreen : PhotosNavigationEvent
    data class NavigateToPhotoDetailsScreen(val photoId: PhotoId) : PhotosNavigationEvent
    data class NavigateToUserDetails(val userNickname: UserNickname) : PhotosNavigationEvent
}