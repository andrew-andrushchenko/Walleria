package com.andrii_a.walleria.ui.topics

import com.andrii_a.walleria.ui.common.TopicId

sealed interface TopicsNavigationEvent {
    data class NavigateToTopicDetails(val topicId: TopicId) : TopicsNavigationEvent
    data object NavigateToSearchScreen : TopicsNavigationEvent
}