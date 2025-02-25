package com.andrii_a.walleria.ui.util

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.annotation.StringRes
import com.andrii_a.walleria.R
import com.andrii_a.walleria.domain.models.photo.PhotoLocation
import com.andrii_a.walleria.ui.common.UserNickname

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
        }, getString(R.string.share)
    ).let {
        startActivity(it)
    }
}

fun Context.openLinkInBrowser(link: String?) {
    CustomTabsHelper.openCustomTab(this, Uri.parse(link))
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

fun Context.openUserProfileInBrowser(userNickname: UserNickname) {
    val link = "https://unsplash.com/@$userNickname"
    this.openLinkInBrowser(link)
}

fun Context.openInstagramProfile(instagramUsername: String) {
    val uri = Uri.parse("https://instagram.com/_u/$instagramUsername")
    Intent(Intent.ACTION_VIEW, uri).apply {
        setPackage("com.instagram.android")
    }.let { instagramIntent ->
        try {
            startActivity(instagramIntent)
        } catch (e: ActivityNotFoundException) {
            val link = "https://instagram.com/$instagramUsername"
            this.openLinkInBrowser(link)
        }
    }
}

fun Context.openTwitterProfile(twitterUsername: String) {
    val uri = Uri.parse("twitter://user?screen_name=$twitterUsername")
    Intent(Intent.ACTION_VIEW, uri).apply {
        setPackage("com.twitter.android")
    }.let { twitterIntent ->
        try {
            startActivity(twitterIntent)
        } catch (e: ActivityNotFoundException) {
            val link = "https://twitter.com/$twitterUsername"
            this.openLinkInBrowser(link)
        }
    }
}

fun Context.openGithubProfile(username: String) {
    val link = "https://github.com/$username"
    this.openLinkInBrowser(link)
}

fun Context.writeALetterTo(email: String) {
    Intent(Intent.ACTION_SENDTO).apply {
        data = Uri.parse("mailto:$email")
        putExtra(Intent.EXTRA_SUBJECT, "")
        putExtra(Intent.EXTRA_TEXT, "")
    }.let {
        startActivity(it)
    }
}