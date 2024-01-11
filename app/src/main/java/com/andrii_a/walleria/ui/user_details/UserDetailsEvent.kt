package com.andrii_a.walleria.ui.user_details

import com.andrii_a.walleria.ui.common.CollectionId
import com.andrii_a.walleria.ui.common.PhotoId
import com.andrii_a.walleria.ui.common.SearchQuery
import com.andrii_a.walleria.ui.common.UserNickname

sealed interface UserDetailsEvent {
    data class RequestUser(val userNickname: UserNickname) : UserDetailsEvent
    data class SelectPhoto(val photoId: PhotoId) : UserDetailsEvent
    data class SelectUser(val userNickname: UserNickname) : UserDetailsEvent
    data class SelectCollection(val collectionId: CollectionId) : UserDetailsEvent
    data class SearchByTag(val query: SearchQuery) : UserDetailsEvent
    data object SelectEditProfile : UserDetailsEvent
    data class SelectPortfolioLink(val url: String?) : UserDetailsEvent
    data class OpenUserProfileInBrowser(val userNickname: UserNickname) : UserDetailsEvent
    data object GoBack : UserDetailsEvent
    //data class SelectLocation() : UserDetailsEvent
    data class SelectInstagramProfile(val instagramNickname: String) : UserDetailsEvent
    data class SelectTwitterProfile(val twitterNickname: String) : UserDetailsEvent
    data object OpenDetailsDialog : UserDetailsEvent
    data object DismissDetailsDialog : UserDetailsEvent
}