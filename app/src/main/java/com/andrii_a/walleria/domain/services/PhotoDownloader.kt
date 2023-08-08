package com.andrii_a.walleria.domain.services

import com.andrii_a.walleria.core.PhotoQuality
import com.andrii_a.walleria.domain.models.photo.Photo

interface PhotoDownloader {

    fun downloadPhoto(photo: Photo, quality: PhotoQuality): Long

}