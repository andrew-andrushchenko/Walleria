package com.andrii_a.walleria.ui.user_details

import com.andrii_a.walleria.ui.common.CollectionId
import com.andrii_a.walleria.ui.common.PhotoId
import com.andrii_a.walleria.ui.common.SearchQuery
import com.andrii_a.walleria.ui.common.UserNickname

sealed interface UserDetailsNavigationEvent {
    data object NavigateBack : UserDetailsNavigationEvent
    data class NavigateToSearchScreen(val query: SearchQuery) : UserDetailsNavigationEvent
    data class NavigateToPhotoDetailsScreen(val photoId: PhotoId) : UserDetailsNavigationEvent
    data class NavigateToCollectionDetails(val collectionId: CollectionId) : UserDetailsNavigationEvent
    data class NavigateToUserDetails(val userNickname: UserNickname) : UserDetailsNavigationEvent
    data object NavigateToEditProfile : UserDetailsNavigationEvent
    data class NavigateToUserProfileInChromeTab(val userNickname: UserNickname) : UserDetailsNavigationEvent
    data class NavigateToChromeCustomTab(val url: String?) : UserDetailsNavigationEvent
    data class NavigateToInstagramApp(val instagramNickname: String) : UserDetailsNavigationEvent
    data class NavigateToTwitterApp(val twitterNickname: String) : UserDetailsNavigationEvent
}