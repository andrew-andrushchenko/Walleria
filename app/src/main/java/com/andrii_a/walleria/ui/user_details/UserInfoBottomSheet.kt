package com.andrii_a.walleria.ui.user_details

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.annotation.StringRes
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.FileDownload
import androidx.compose.material.icons.outlined.GroupAdd
import androidx.compose.material.icons.outlined.PeopleOutline
import androidx.compose.material.icons.outlined.Photo
import androidx.compose.material.icons.outlined.PhotoAlbum
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.andrii_a.walleria.R
import com.andrii_a.walleria.domain.models.common.Tag
import com.andrii_a.walleria.domain.models.user.User
import com.andrii_a.walleria.domain.models.user.UserTags
import com.andrii_a.walleria.ui.common.SearchQuery
import com.andrii_a.walleria.ui.common.components.TagsRow
import com.andrii_a.walleria.ui.theme.WalleriaTheme
import com.andrii_a.walleria.ui.util.abbreviatedNumberString

@Composable
fun UserInfoBottomSheet(
    user: User,
    contentPadding: PaddingValues = PaddingValues(),
    navigateToSearch: (SearchQuery) -> Unit
) {
    Column(modifier = Modifier.padding(contentPadding)) {
        user.bio?.let {
            var isExpanded by rememberSaveable { mutableStateOf(false) }

            Text(
                text = it,
                style = MaterialTheme.typography.titleSmall,
                maxLines = if (isExpanded) Int.MAX_VALUE else 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .align(Alignment.CenterHorizontally)
                    .animateContentSize()
                    .clickable { isExpanded = !isExpanded }
            )

            Spacer(modifier = Modifier.height(16.dp))
        }

        user.tags?.custom?.let {
            TagsRow(
                tags = it,
                onTagClicked = { query ->
                    navigateToSearch(SearchQuery(query))
                },
                contentPadding = PaddingValues(horizontal = 16.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))
        }

        UserStatistics(
            user = user,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun UserStatsItem(
    icon: ImageVector,
    @StringRes titleRes: Int,
    value: Long,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = stringResource(id = titleRes)
            )

            Spacer(modifier = Modifier.width(4.dp))

            Text(
                text = stringResource(id = titleRes),
                style = MaterialTheme.typography.titleSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = value.abbreviatedNumberString,
            style = MaterialTheme.typography.bodySmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun UserStatistics(
    user: User,
    modifier: Modifier = Modifier
) {
    // Triples of (icon, title, value)
    val statsGridItems = listOf(
        Triple(Icons.Outlined.FileDownload, R.string.downloads, user.downloads),
        Triple(Icons.Outlined.Photo, R.string.photos, user.totalPhotos),
        Triple(Icons.Outlined.FavoriteBorder, R.string.total_likes, user.totalLikes),
        Triple(Icons.Outlined.PhotoAlbum, R.string.collections, user.totalCollections),
        Triple(Icons.Outlined.PeopleOutline, R.string.followers, user.followersCount),
        Triple(Icons.Outlined.GroupAdd, R.string.following, user.followingCount)
    )

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
    ) {
        items(statsGridItems) { item ->
            UserStatsItem(icon = item.first, titleRes = item.second, value = item.third)
        }
    }
}

@Preview(uiMode = UI_MODE_NIGHT_NO)
@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
fun UserInfoBottomSheetPreview() {
    WalleriaTheme {
        val tags = "Lorem ipsum dolor sit amet another very interesting text"
            .split(" ")
            .map { Tag(it) }

        val user = User(
            id = "",
            username = "ABC",
            firstName = "John",
            lastName = "Smith",
            bio = "Lorem ipsum dolor sit amet".repeat(10),
            location = "San Francisco, California, USA",
            totalLikes = 100,
            totalPhotos = 100,
            totalCollections = 100,
            followersCount = 100_000,
            followingCount = 56,
            downloads = 99_000,
            profileImage = null,
            social = null,
            tags = UserTags(tags),
            photos = null
        )

        Surface {
            UserInfoBottomSheet(user = user, navigateToSearch = {})
        }
    }
}