package com.andrii_a.walleria.ui.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.FolderSpecial
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material.icons.filled.PhotoAlbum
import androidx.compose.material.icons.outlined.AccountBox
import androidx.compose.material.icons.outlined.FolderSpecial
import androidx.compose.material.icons.outlined.Photo
import androidx.compose.material.icons.outlined.PhotoAlbum
import androidx.compose.ui.graphics.vector.ImageVector
import com.andrii_a.walleria.R

enum class NavigationScreen(
    val route: Screen,
    @StringRes val titleRes: Int,
    val iconUnselected: ImageVector,
    val iconSelected: ImageVector
) {
    Photos(
        route = Screen.Photos,
        titleRes = R.string.photos,
        iconUnselected = Icons.Outlined.Photo,
        iconSelected = Icons.Filled.Photo
    ),
    Collections(
        route = Screen.Collections,
        titleRes = R.string.collections,
        iconUnselected = Icons.Outlined.PhotoAlbum,
        iconSelected = Icons.Filled.PhotoAlbum
    ),
    Topics(
        route = Screen.Topics,
        titleRes = R.string.topics,
        iconUnselected = Icons.Outlined.FolderSpecial,
        iconSelected = Icons.Filled.FolderSpecial
    ),
    AccountAndSettings(
        route = Screen.AccountAndSettings,
        titleRes = R.string.account,
        iconUnselected = Icons.Outlined.AccountBox,
        iconSelected = Icons.Filled.AccountBox
    )
}
