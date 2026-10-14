package com.andrii_a.walleria.ui.topic_details

import androidx.compose.runtime.Stable
import androidx.paging.PagingData
import com.andrii_a.walleria.domain.PhotoQuality
import com.andrii_a.walleria.domain.models.photo.Photo
import com.andrii_a.walleria.domain.models.topic.Topic
import com.andrii_a.walleria.ui.common.UiError
import com.andrii_a.walleria.ui.util.emptyPagingData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

@Stable
data class TopicDetailsUiState(
    val isLoading: Boolean = false,
    val error: UiError? = null,
    val topic: Topic? = null,
    private val topicPhotosPagingData: PagingData<Photo> = emptyPagingData(),
    val topicPhotosFilters: TopicPhotosFilters = TopicPhotosFilters(),
    val isFilterDialogOpened: Boolean = false,
    val photosLoadQuality: PhotoQuality = PhotoQuality.MEDIUM,
) {
    val topicPhotos: StateFlow<PagingData<Photo>> field = MutableStateFlow(emptyPagingData())

    init {
        topicPhotos.update { topicPhotosPagingData }
    }
}
