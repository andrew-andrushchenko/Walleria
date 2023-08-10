package com.andrii_a.walleria.ui.user_details

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.andrii_a.walleria.R
import com.andrii_a.walleria.domain.models.common.Tag
import com.andrii_a.walleria.domain.models.user.User
import com.andrii_a.walleria.domain.models.user.UserTags
import com.andrii_a.walleria.ui.common.SearchQuery
import com.andrii_a.walleria.ui.photo_details.components.TagsRow
import com.andrii_a.walleria.ui.theme.WalleriaTheme
import com.andrii_a.walleria.ui.util.abbreviatedNumberString

@Composable
fun UserInfoBottomSheet(
    user: User,
    navigateToSearch: (SearchQuery) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
    ) {
        Column {
            Spacer(
                modifier = Modifier
                    .padding(vertical = 22.dp)
                    .size(width = 32.dp, height = 4.dp)
                    .background(
                        color = MaterialTheme.colors.onSurface.copy(alpha = 0.4f),
                        shape = RoundedCornerShape(50)
                    )
                    .align(Alignment.CenterHorizontally)
            )

            user.bio?.let {
                var isExpanded by rememberSaveable { mutableStateOf(false) }

                Text(
                    text = it,
                    style = MaterialTheme.typography.subtitle2,
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
                // TODO(Andrii): Move to common components
                TagsRow(
                    tags = it,
                    onTagClicked = { query ->
                        navigateToSearch(SearchQuery(query))
                    },
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
}

@Composable
private fun UserStatsItem(
    @DrawableRes icon: Int,
    @StringRes titleRes: Int,
    value: Long,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = stringResource(id = titleRes)
            )

            Spacer(modifier = Modifier.width(4.dp))

            Text(
                text = stringResource(id = titleRes),
                style = MaterialTheme.typography.subtitle2,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = value.abbreviatedNumberString,
            style = MaterialTheme.typography.caption,
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
    val statsGridItems = listOf<Triple<@DrawableRes Int, @StringRes Int, Long>>(
        Triple(R.drawable.ic_download_outlined, R.string.downloads, user.downloads),
        Triple(R.drawable.ic_photos_outlined, R.string.photos, user.totalPhotos),
        Triple(R.drawable.ic_liked_photo_outlined, R.string.total_likes, user.totalLikes),
        Triple(R.drawable.ic_collection_outlined, R.string.collections, user.totalCollections),
        Triple(R.drawable.ic_followers_outlined, R.string.followers, user.followersCount),
        Triple(R.drawable.ic_following_outlined, R.string.following, user.followingCount)
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

        UserInfoBottomSheet(user = user, navigateToSearch = {})
    }
}