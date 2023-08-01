package com.andrii_a.walleria.ui.common

@JvmInline
value class PhotoId(val value: String)

@JvmInline
value class CollectionId(val value: String)

@JvmInline
value class SearchQuery(val value: String)

@JvmInline
value class UserNickname(val value: String)

data class TopicInfo(
    val idAsString: String,
    val title: String?
)