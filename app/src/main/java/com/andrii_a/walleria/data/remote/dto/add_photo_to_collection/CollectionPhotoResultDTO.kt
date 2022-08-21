package com.andrii_a.walleria.data.remote.dto.add_photo_to_collection

import com.andrii_a.walleria.domain.models.add_photo_to_collection.CollectionPhotoResult
import com.andrii_a.walleria.data.remote.dto.collection.CollectionDTO
import com.andrii_a.walleria.data.remote.dto.photo.PhotoDTO

data class CollectionPhotoResultDTO(
    val photo: PhotoDTO?,
    val collection: CollectionDTO?
) {
    fun toCollectionPhotoResult(): CollectionPhotoResult = CollectionPhotoResult(
        photo = photo?.toPhoto(),
        collection = collection?.toCollection()
    )
}