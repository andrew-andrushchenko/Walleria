package com.andrii_a.walleria.ui.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material.icons.filled.Topic
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Collections
import androidx.compose.material.icons.outlined.Photo
import androidx.compose.material.icons.outlined.Topic
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