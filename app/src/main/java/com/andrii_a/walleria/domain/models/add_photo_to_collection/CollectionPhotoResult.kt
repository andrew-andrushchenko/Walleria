package com.andrii_a.walleria.domain.models.add_photo_to_collection

import com.andrii_a.walleria.domain.models.collection.Collection
import com.andrii_a.walleria.domain.models.photo.Photo

data class CollectionPhotoResult(
    val photo: Photo?,
    val collection: Collection?
)