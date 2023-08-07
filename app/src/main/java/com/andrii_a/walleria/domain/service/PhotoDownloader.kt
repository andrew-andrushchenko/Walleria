package com.andrii_a.walleria.domain.service

import com.andrii_a.walleria.core.PhotoQuality
import com.andrii_a.walleria.domain.models.photo.Photo

interface PhotoDownloader {

    fun downloadPhoto(photo: Photo, quality: PhotoQuality): Long

}