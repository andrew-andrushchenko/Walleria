package com.andrii_a.walleria.ui.photo_details

import com.andrii_a.walleria.ui.common.CollectionId
import com.andrii_a.walleria.ui.common.PhotoId
import com.andrii_a.walleria.ui.common.SearchQuery
import com.andrii_a.walleria.ui.common.UserNickname

sealed interface PhotoDetailsNavigationEvent {
    data object NavigateBack : PhotoDetailsNavigationEvent
    data class NavigateToUserDetails(val userNickname: UserNickname) : PhotoDetailsNavigationEvent
    data class NavigateToCollectPhoto(val photoId: PhotoId) : PhotoDetailsNavigationEvent
    data class NavigateToCollectionDetails(val collectionId: CollectionId) : PhotoDetailsNavigationEvent
    data class NavigateToSearch(val query: SearchQuery) : PhotoDetailsNavigationEvent
    data class NavigateToChromeCustomTab(val url: String?) : PhotoDetailsNavigationEvent
    data class NavigateToShareDialog(
        val link: String?,
        val description: String?
    ) : PhotoDetailsNavigationEvent
    data object NavigateToLogin : PhotoDetailsNavigationEvent
}