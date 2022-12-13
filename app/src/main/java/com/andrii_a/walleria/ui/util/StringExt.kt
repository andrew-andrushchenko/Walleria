package com.andrii_a.walleria.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.andrii_a.walleria.R
import java.text.SimpleDateFormat
import java.util.*

private const val SECOND_MILLIS = 1000
private const val MINUTE_MILLIS = 60 * SECOND_MILLIS
private const val HOUR_MILLIS = 60 * MINUTE_MILLIS
private const val DAY_MILLIS = 24 * HOUR_MILLIS

val String?.timeAgoLocalizedString: String?
    @Composable
    get() {
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ROOT)
        val date = format.parse(this.orEmpty())
        var time = date?.time ?: 0L

        if (time < 1000000000000L) {
            time *= 1000
        }

        val now = System.currentTimeMillis()
        if (time > now || time <= 0) {
            return null
        }

        val diff = now - time

        return when {
            diff < MINUTE_MILLIS -> stringResource(id = R.string.just_now)
            diff < 2 * MINUTE_MILLIS -> stringResource(id = R.string.minute_ago)
            diff < 50 * MINUTE_MILLIS -> stringResource(
                id = R.string.minutes_ago_formatted,
                diff / MINUTE_MILLIS
            )
            diff < 90 * MINUTE_MILLIS -> stringResource(id = R.string.hour_ago)
            diff < 24 * HOUR_MILLIS -> stringResource(
                id = R.string.hours_ago_formatted,
                diff / HOUR_MILLIS
            )
            diff < 48 * HOUR_MILLIS -> stringResource(id = R.string.yesterday)
            else -> stringResource(id = R.string.days_ago_formatted, diff / DAY_MILLIS)
        }
    }