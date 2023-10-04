package com.andrii_a.walleria.ui.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FolderSpecial
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material.icons.filled.PhotoAlbum
import androidx.compose.material.icons.outlined.FolderSpecial
import androidx.compose.material.icons.outlined.Photo
import androidx.compose.material.icons.outlined.PhotoAlbum
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
        iconUnselected = Icons.Outlined.PhotoAlbum,
        iconSelected = Icons.Filled.PhotoAlbum
    ),
    Topics(
        route = "topics",
        titleRes = R.string.topics,
        iconUnselected = Icons.Outlined.FolderSpecial,
        iconSelected = Icons.Filled.FolderSpecial
    )
}

val NavigationScreenRoutes: List<String> by lazy {
    NavigationScreen.values().map { it.route }
}