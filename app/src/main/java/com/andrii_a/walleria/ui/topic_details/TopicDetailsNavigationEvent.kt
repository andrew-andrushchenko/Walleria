package com.andrii_a.walleria.ui.topic_details

import com.andrii_a.walleria.ui.common.PhotoId
import com.andrii_a.walleria.ui.common.UserNickname
import java.net.URI

sealed interface TopicDetailsNavigationEvent {
    data object NavigateBack : TopicDetailsNavigationEvent
    data class NavigateToUserDetails(val userNickname: UserNickname) : TopicDetailsNavigationEvent
    data class NavigateToPhotoDetails(val photoId: PhotoId) : TopicDetailsNavigationEvent
    data class NavigateToChromeCustomTab(val url: String?) : TopicDetailsNavigationEvent
}