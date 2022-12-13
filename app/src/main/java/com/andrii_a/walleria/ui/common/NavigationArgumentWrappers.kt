package com.andrii_a.walleria.ui.common

@JvmInline
value class PhotoId(val value: String)

@JvmInline
value class UserNickname(val value: String)

data class CollectionInfo(
    val idAsString: String,
    val title: String,
    val totalPhotos: Int,
    val userNickname: String,
    val userFullName: String,
    val description: String?,
    val isPrivate: Boolean
)

data class TopicInfo(
    val idAsString: String,
    val title: String?
)