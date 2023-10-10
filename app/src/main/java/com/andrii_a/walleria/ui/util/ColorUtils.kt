package com.andrii_a.walleria.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.toArgb
import kotlin.math.sqrt
import android.graphics.Color as AndroidColor
import androidx.compose.ui.graphics.Color as ComposeColor

fun isBrightColor(color: Int): Boolean {
    if (android.R.color.transparent == color) return true
    val rgb =
        intArrayOf(AndroidColor.red(color), AndroidColor.green(color), AndroidColor.blue(color))
    val brightness = sqrt(
        rgb[0] * rgb[0] * .241 + (rgb[1] * rgb[1] * .691) + rgb[2] * rgb[2] * .068
    ).toInt()
    return brightness >= 100
}

@Composable
fun contentColorFor(color: ComposeColor): ComposeColor {
    return if (isBrightColor(color.toArgb())) ComposeColor.Black else ComposeColor.White
}
