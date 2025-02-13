package com.andrii_a.walleria.ui.topics

import com.andrii_a.walleria.ui.common.TopicId

sealed interface TopicsEvent {
    data class ChangeListOrder(val orderOptionOrdinalNum: Int) : TopicsEvent
    data class SelectTopic(val topicId: TopicId) : TopicsEvent
    data object SelectSearch : TopicsEvent
    data class ToggleListOrderMenu(val isExpanded: Boolean) : TopicsEvent
}