package com.andrii_a.walleria.ui.photo_details

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.database.Cursor
import com.andrii_a.walleria.R
import com.andrii_a.walleria.core.ApplicationScope
import com.andrii_a.walleria.domain.repository.PhotoRepository
import com.andrii_a.walleria.ui.util.toast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@AndroidEntryPoint
class DownloadCompletedReceiver : BroadcastReceiver() {

    @Inject
    lateinit var photoRepository: PhotoRepository

    @Inject
    @ApplicationScope
    lateinit var applicationScope: CoroutineScope

    private lateinit var downloadManager: DownloadManager

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action != DownloadManager.ACTION_DOWNLOAD_COMPLETE) {
            return
        }

        downloadManager = context?.getSystemService(DownloadManager::class.java) ?: return

        val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1L)

        if (id == -1L) {
            return
        }

        val query = DownloadManager.Query().setFilterById(id)
        val cursor = downloadManager.query(query) ?: return

        if (!cursor.moveToFirst()) {
            return
        }

        @SuppressLint("Range")
        when (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))) {
            DownloadManager.STATUS_SUCCESSFUL -> {
                context.toast(context.getString(R.string.download_complete))
                trackDownload(cursor)
            }

            else -> {
                context.toast(context.getString(R.string.download_failed))
            }
        }
    }

    private fun trackDownload(cursor: Cursor) {
        @SuppressLint("Range")
        val downloadedPhotoUri = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI))
        val downloadedPhoto = File(downloadedPhotoUri)

        val photoId = downloadedPhoto.name.substringBefore('_')

        applicationScope.launch {
            photoRepository.trackPhotoDownload(photoId)
        }
    }

}