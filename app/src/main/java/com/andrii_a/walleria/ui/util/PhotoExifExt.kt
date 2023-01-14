package com.andrii_a.walleria.ui.util

import com.andrii_a.walleria.domain.models.photo.PhotoExif
import java.util.*

fun PhotoExif.formCameraNameOrEmpty(): String {
    val makeList = this.make?.split(" ")?.map { it.trim() }
    val modelList = this.model?.split(" ")?.map { it.trim() }
    return if (makeList?.map { it.lowercase(Locale.ROOT) }
            ?.intersect((modelList?.map { it.lowercase(Locale.ROOT) } ?: emptyList()).toSet())
            ?.isEmpty() == true
    ) {
        "${makeList.first()} $model"
    } else {
        model.orEmpty()
    }
}
