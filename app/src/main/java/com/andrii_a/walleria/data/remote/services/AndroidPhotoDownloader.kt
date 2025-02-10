package com.andrii_a.walleria.data.remote.services

import android.app.DownloadManager
import android.content.Context
import android.os.Environment
import androidx.core.net.toUri
import com.andrii_a.walleria.R
import com.andrii_a.walleria.domain.PhotoQuality
import com.andrii_a.walleria.domain.models.photo.Photo
import com.andrii_a.walleria.domain.services.DownloadId
import com.andrii_a.walleria.domain.services.PhotoDownloader
import com.andrii_a.walleria.ui.util.downloadFilename
import com.andrii_a.walleria.ui.util.getUrlByQuality

class AndroidPhotoDownloader(private val context: Context) : PhotoDownloader {

    private val downloadManager: DownloadManager by lazy {
        context.getSystemService(DownloadManager::class.java)
    }

    override fun downloadPhoto(photo: Photo, quality: PhotoQuality): DownloadId {
        val photoUri = photo.getUrlByQuality(quality).toUri()

        val request = DownloadManager.Request(photoUri).apply {
            setMimeType("image/jpeg")
            setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
            setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            setTitle(photo.downloadFilename)
            setDescription(context.getString(R.string.downloading_photo))

            val subPath = "${context.getString(R.string.app_name)}/${photo.downloadFilename}"
            setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, subPath)
        }

        return downloadManager.enqueue(request)
    }
}