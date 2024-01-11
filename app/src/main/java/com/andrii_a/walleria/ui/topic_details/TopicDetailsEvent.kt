package com.andrii_a.walleria.ui.topic_details

import com.andrii_a.walleria.ui.common.PhotoId
import com.andrii_a.walleria.ui.common.TopicId
import com.andrii_a.walleria.ui.common.UserNickname

sealed interface TopicDetailsEvent {
    data class RequestTopic(val topicId: TopicId) : TopicDetailsEvent
    data class ChangeFilters(val topicPhotosFilters: TopicPhotosFilters) : TopicDetailsEvent
    data object GoBack : TopicDetailsEvent
    data class OpenInBrowser(val url: String?) : TopicDetailsEvent
    data object OpenFilterDialog : TopicDetailsEvent
    data object DismissFilterDialog : TopicDetailsEvent
    data class SelectPhoto(val photoId: PhotoId) : TopicDetailsEvent
    data class SelectUser(val userNickname: UserNickname) : TopicDetailsEvent
}