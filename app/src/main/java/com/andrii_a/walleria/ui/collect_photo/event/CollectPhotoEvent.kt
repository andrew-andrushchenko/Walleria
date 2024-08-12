package com.andrii_a.walleria.ui.collect_photo.event

import com.andrii_a.walleria.ui.common.PhotoId

sealed interface CollectPhotoEvent {
    data class CollectPhoto(
        val collectionId: String,
        val photoId: String
    ) : CollectPhotoEvent

    data class DropPhotoFromCollection(
        val collectionId: String,
        val photoId: String
    ) : CollectPhotoEvent

    data class CreateCollectionAndCollect(
        val title: String,
        val description: String? = null,
        val isPrivate: Boolean = false,
        val photoId: PhotoId
    ) : CollectPhotoEvent

    data object GoBack : CollectPhotoEvent

    data object OpenCreateAndCollectDialog : CollectPhotoEvent
    data object DismissCreateAndCollectDialog : CollectPhotoEvent
}