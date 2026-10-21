package com.andrii_a.walleria.data.services

import com.andrii_a.walleria.domain.PhotoQuality
import com.andrii_a.walleria.domain.models.photo.Photo
import com.andrii_a.walleria.domain.services.DownloadId
import com.andrii_a.walleria.domain.services.PhotoDownloader

class FakePhotoDownloader : PhotoDownloader {

    var downloadedPhoto: Photo? = null
    var downloadedQuality: PhotoQuality? = null
    var downloadCallCount: Int = 0

    override fun downloadPhoto(photo: Photo, quality: PhotoQuality): DownloadId {
        downloadedPhoto = photo
        downloadedQuality = quality
        downloadCallCount++
        return downloadCallCount.toLong()
    }
}
