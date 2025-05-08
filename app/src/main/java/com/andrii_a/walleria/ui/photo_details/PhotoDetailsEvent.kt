package com.andrii_a.walleria.ui.photo_details

import com.andrii_a.walleria.domain.PhotoQuality
import com.andrii_a.walleria.domain.models.photo.Photo
import com.andrii_a.walleria.ui.common.CollectionId
import com.andrii_a.walleria.ui.common.PhotoId
import com.andrii_a.walleria.ui.common.SearchQuery
import com.andrii_a.walleria.ui.common.UserNickname

sealed interface PhotoDetailsEvent {
    data class RequestPhoto(val photoId: PhotoId) : PhotoDetailsEvent

    data class LikePhoto(val photoId: PhotoId) : PhotoDetailsEvent

    data class DislikePhoto(val photoId: PhotoId) : PhotoDetailsEvent

    data object MakeCollected : PhotoDetailsEvent

    data object MakeDropped : PhotoDetailsEvent

    data class DownloadPhoto(
        val photo: Photo,
        val quality: PhotoQuality = PhotoQuality.HIGH
    ) : PhotoDetailsEvent

    data class OpenInBrowser(val url: String?) : PhotoDetailsEvent

    data class SharePhoto(val link: String?, val description: String?) : PhotoDetailsEvent

    data object ShowInfoDialog : PhotoDetailsEvent

    data object DismissInfoDialog : PhotoDetailsEvent

    data object GoBack : PhotoDetailsEvent

    data class SearchByTag(val query: SearchQuery) : PhotoDetailsEvent

    data class SelectUser(val userNickname: UserNickname) : PhotoDetailsEvent

    data class SelectCollection(val collectionId: CollectionId) : PhotoDetailsEvent

    data class SelectCollectOption(val photoId: PhotoId) : PhotoDetailsEvent

    data object RedirectToLogin : PhotoDetailsEvent

    data object ToggleControlsVisibility : PhotoDetailsEvent

    data class UpdateZoomToFillCoefficient(
        val imageWidth: Float,
        val imageHeight: Float,
        val containerWidth: Float,
        val containerHeight: Float
    ) : PhotoDetailsEvent
}