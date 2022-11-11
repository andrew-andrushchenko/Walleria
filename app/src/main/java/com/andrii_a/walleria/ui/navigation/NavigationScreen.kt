package com.andrii_a.walleria.ui.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.graphics.vector.ImageVector
import com.andrii_a.walleria.R

enum class NavigationScreen(
    val route: String,
    @StringRes val titleRes: Int,
    val iconUnselected: ImageVector,
    val iconSelected: ImageVector
) {
    Photos(
        route = "photos",
        titleRes = R.string.photos,
        iconUnselected = Icons.Outlined.Photo,
        iconSelected = Icons.Filled.Photo
    ),
    Collections(
        route = "collections",
        titleRes = R.string.collections,
        iconUnselected = Icons.Outlined.Collections,
        iconSelected = Icons.Filled.Collections
    ),
    Topics(
        route = "topics",
        titleRes = R.string.topics,
        iconUnselected = Icons.Outlined.Topic,
        iconSelected = Icons.Filled.Topic
    ),
    Search(
        route = "search",
        titleRes = R.string.search,
        iconUnselected = Icons.Outlined.Search,
        iconSelected = Icons.Filled.Search
    ),
    Profile(
        route = "profile",
        titleRes = R.string.profile,
        iconUnselected = Icons.Outlined.AccountCircle,
        iconSelected = Icons.Filled.AccountCircle
    )
}

val NavigationScreenRoutes: List<String> by lazy {
    NavigationScreen.values().map { it.route }
}