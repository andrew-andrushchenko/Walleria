package com.andrii_a.walleria.ui.util

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.annotation.StringRes

fun Context.startActivity(cls: Class<*>) {
    startActivity(Intent(this, cls))
}

fun Context.toast(@StringRes stringRes: Int, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, getString(stringRes), duration).show()
}