package com.andrii_a.walleria.ui.topic_details

import androidx.paging.PagingData
import com.andrii_a.walleria.domain.PhotoQuality
import com.andrii_a.walleria.domain.PhotosListLayoutType
import com.andrii_a.walleria.domain.models.photo.Photo
import com.andrii_a.walleria.domain.models.topic.Topic
import com.andrii_a.walleria.ui.common.UiError

data class TopicDetailsUiState(
    val isLoading: Boolean = false,
    val error: UiError? = null,
    val topic: Topic? = null,
    val topicPhotosPagingData: PagingData<Photo> = PagingData.empty(),
    val topicPhotosFilters: TopicPhotosFilters = TopicPhotosFilters(),
    val isFilterDialogOpened: Boolean = false,
    val photosListLayoutType: PhotosListLayoutType = PhotosListLayoutType.DEFAULT,
    val photosLoadQuality: PhotoQuality = PhotoQuality.MEDIUM,
)
