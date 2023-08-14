package com.andrii_a.walleria.ui.util

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.andrii_a.walleria.R
import com.andrii_a.walleria.domain.CollectionListLayoutType
import com.andrii_a.walleria.domain.PhotoListDisplayOrder
import com.andrii_a.walleria.domain.PhotoQuality
import com.andrii_a.walleria.domain.PhotosListLayoutType
import com.andrii_a.walleria.domain.SearchResultsContentFilter
import com.andrii_a.walleria.domain.SearchResultsDisplayOrder
import com.andrii_a.walleria.domain.SearchResultsPhotoColor
import com.andrii_a.walleria.domain.SearchResultsPhotoOrientation
import com.andrii_a.walleria.domain.TopicPhotosOrientation
import com.andrii_a.walleria.domain.TopicStatus
import com.andrii_a.walleria.domain.TopicsDisplayOrder

val PhotoListDisplayOrder.titleRes: Int
    @StringRes
    get() = when (this) {
        PhotoListDisplayOrder.LATEST -> R.string.order_latest
        PhotoListDisplayOrder.OLDEST -> R.string.order_oldest
        PhotoListDisplayOrder.POPULAR -> R.string.order_popular
    }

val SearchResultsDisplayOrder.titleRes: Int
    @StringRes
    get() = when (this) {
        SearchResultsDisplayOrder.RELEVANT -> R.string.filter_order_relevance
        SearchResultsDisplayOrder.LATEST -> R.string.filter_order_latest
    }

val SearchResultsContentFilter.titleRes: Int
    @StringRes
    get() = when (this) {
        SearchResultsContentFilter.LOW -> R.string.content_filter_low
        SearchResultsContentFilter.HIGH -> R.string.content_filter_high
    }

val SearchResultsPhotoColor.titleRes: Int
    @StringRes
    get() = when (this) {
        SearchResultsPhotoColor.ANY -> R.string.filter_color_any
        SearchResultsPhotoColor.BLACK_AND_WHITE -> R.string.filter_color_black_white
        SearchResultsPhotoColor.BLACK -> R.string.filter_color_black
        SearchResultsPhotoColor.WHITE -> R.string.filter_color_white
        SearchResultsPhotoColor.YELLOW -> R.string.filter_color_yellow
        SearchResultsPhotoColor.ORANGE -> R.string.filter_color_orange
        SearchResultsPhotoColor.RED -> R.string.filter_color_red
        SearchResultsPhotoColor.PURPLE -> R.string.filter_color_purple
        SearchResultsPhotoColor.MAGENTA -> R.string.filter_color_magenta
        SearchResultsPhotoColor.GREEN -> R.string.filter_color_green
        SearchResultsPhotoColor.TEAL -> R.string.filter_color_teal
        SearchResultsPhotoColor.BLUE -> R.string.filter_color_blue
    }

val SearchResultsPhotoOrientation.titleRes: Int
    @StringRes
    get() = when (this) {
        SearchResultsPhotoOrientation.ANY -> R.string.orientation_any
        SearchResultsPhotoOrientation.LANDSCAPE -> R.string.orientation_landscape
        SearchResultsPhotoOrientation.PORTRAIT -> R.string.orientation_portrait
        SearchResultsPhotoOrientation.SQUARISH -> R.string.orientation_squarish
    }

val SearchResultsPhotoOrientation.iconRes: Int
    @DrawableRes
    get() = when (this) {
        SearchResultsPhotoOrientation.ANY -> 0
        SearchResultsPhotoOrientation.LANDSCAPE -> R.drawable.ic_landscape
        SearchResultsPhotoOrientation.PORTRAIT -> R.drawable.ic_portrait
        SearchResultsPhotoOrientation.SQUARISH -> R.drawable.ic_squarish
    }

val TopicPhotosOrientation.titleRes: Int
    @StringRes
    get() = when (this) {
        TopicPhotosOrientation.LANDSCAPE -> R.string.orientation_landscape
        TopicPhotosOrientation.PORTRAIT -> R.string.orientation_portrait
        TopicPhotosOrientation.SQUARISH -> R.string.orientation_squarish
    }

val TopicPhotosOrientation.iconRes: Int
    @DrawableRes
    get() = when (this) {
        TopicPhotosOrientation.LANDSCAPE -> R.drawable.ic_landscape
        TopicPhotosOrientation.PORTRAIT -> R.drawable.ic_portrait
        TopicPhotosOrientation.SQUARISH -> R.drawable.ic_squarish
    }

val TopicsDisplayOrder.titleRes: Int
    @StringRes
    get() = when (this) {
        TopicsDisplayOrder.FEATURED -> R.string.order_featured
        TopicsDisplayOrder.LATEST -> R.string.order_latest
        TopicsDisplayOrder.OLDEST -> R.string.order_oldest
        TopicsDisplayOrder.POSITION -> R.string.order_position
    }

val TopicStatus.titleRes: Int
    @StringRes
    get() = when (this) {
        TopicStatus.OPEN -> R.string.topic_status_open
        TopicStatus.CLOSED -> R.string.topic_status_closed
        TopicStatus.OTHER -> R.string.topic_status_other
    }

val PhotoQuality.titleRes: Int
    @StringRes
    get() = when (this) {
        PhotoQuality.RAW -> R.string.photo_quality_raw
        PhotoQuality.HIGH -> R.string.photo_quality_high
        PhotoQuality.MEDIUM -> R.string.photo_quality_medium
        PhotoQuality.LOW -> R.string.photo_quality_low
        PhotoQuality.THUMBNAIL -> R.string.photo_quality_thumbnail
    }

val PhotosListLayoutType.titleRes: Int
    @StringRes
    get() = when (this) {
        PhotosListLayoutType.DEFAULT -> R.string.type_default
        PhotosListLayoutType.MINIMAL_LIST -> R.string.type_minimal_list
        PhotosListLayoutType.STAGGERED_GRID -> R.string.type_staggered_grid
    }

val CollectionListLayoutType.titleRes: Int
    @StringRes
    get() = when (this) {
        CollectionListLayoutType.DEFAULT -> R.string.type_default
        CollectionListLayoutType.MINIMAL_LIST -> R.string.type_minimal_list
        CollectionListLayoutType.GRID -> R.string.type_grid
    }
