package com.andrii_a.walleria.data.remote.dto.collect_photo

import com.andrii_a.walleria.domain.models.collect_photo.CollectionPhotoResult
import com.andrii_a.walleria.data.remote.dto.collection.CollectionDto
import com.andrii_a.walleria.data.remote.dto.photo.PhotoDto
import kotlinx.serialization.Serializable

@Serializable
data class CollectionPhotoResultDto(
    val photo: PhotoDto? = null,
    val collection: CollectionDto? = null
) {
    fun toCollectionPhotoResult(): CollectionPhotoResult = CollectionPhotoResult(
        photo = photo?.toPhoto(),
        collection = collection?.toCollection()
    )
}