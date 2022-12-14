package com.andrii_a.walleria.ui.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.andrii_a.walleria.R

enum class NavigationScreen(
    val route: String,
    @StringRes val titleRes: Int,
    @DrawableRes val iconUnselected: Int,
    @DrawableRes val iconSelected: Int
) {
    Photos(
        route = "photos",
        titleRes = R.string.photos,
        iconUnselected = R.drawable.ic_photos_outlined,
        iconSelected = R.drawable.ic_photos_filled
    ),
    Collections(
        route = "collections",
        titleRes = R.string.collections,
        iconUnselected = R.drawable.ic_collection_outlined,
        iconSelected = R.drawable.ic_collection_filled
    ),
    Topics(
        route = "topics",
        titleRes = R.string.topics,
        iconUnselected = R.drawable.ic_topic_outlined,
        iconSelected = R.drawable.ic_topic_filled
    ),
    Search(
        route = "search",
        titleRes = R.string.search,
        iconUnselected = R.drawable.ic_search_outlined,
        iconSelected = R.drawable.ic_search_filled
    )
}

val NavigationScreenRoutes: List<String> by lazy {
    NavigationScreen.values().map { it.route }
}