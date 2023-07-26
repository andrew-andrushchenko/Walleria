package com.andrii_a.walleria.domain.models.collect_photo

import com.andrii_a.walleria.domain.models.collection.Collection
import com.andrii_a.walleria.domain.models.photo.Photo

data class CollectionPhotoResult(
    val photo: Photo?,
    val collection: Collection?
)