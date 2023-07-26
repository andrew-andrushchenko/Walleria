package com.andrii_a.walleria.ui.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.annotation.StringRes
import com.andrii_a.walleria.domain.models.photo.PhotoLocation

fun Context.startActivity(cls: Class<*>) {
    startActivity(Intent(this, cls))
}

fun Context.toast(@StringRes stringRes: Int, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, getString(stringRes), duration).show()
}

fun Context.toast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, duration).show()
}

fun Context.sharePhoto(photoLink: String?, photoDescription: String?) {
    if (photoLink == null) return

    Intent.createChooser(
        Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, photoLink)
            putExtra(Intent.EXTRA_TITLE, photoDescription)
        }, "Share"
    ).let {
        startActivity(it)
    }
}

fun Context.openPhotoInBrowser(photoLink: String?) {
    CustomTabsHelper.openCustomTab(this, Uri.parse(photoLink))
}

fun Context.openLocationInMaps(position: PhotoLocation.Position?) {
    position?.let {
        startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse("geo:${it.latitude},${it.longitude}")
            )
        )
    }
}