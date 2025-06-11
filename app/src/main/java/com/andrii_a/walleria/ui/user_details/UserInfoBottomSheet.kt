package com.andrii_a.walleria.ui.user_details

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.annotation.StringRes
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.andrii_a.walleria.R
import com.andrii_a.walleria.domain.models.common.Tag
import com.andrii_a.walleria.domain.models.user.User
import com.andrii_a.walleria.domain.models.user.UserSocialMediaLinks
import com.andrii_a.walleria.domain.models.user.UserTags
import com.andrii_a.walleria.ui.common.SearchQuery
import com.andrii_a.walleria.ui.common.components.TagsRow
import com.andrii_a.walleria.ui.theme.WalleriaTheme
import com.andrii_a.walleria.ui.util.abbreviatedNumberString

@Composable
fun UserInfoBottomSheet(
    user: User,
    navigateToSearch: (SearchQuery) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        UserStatistics(
            user = user,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        HorizontalDivider(modifier = Modifier.padding(16.dp))

        user.bio?.let {
            var isExpanded by rememberSaveable { mutableStateOf(false) }

            Text(
                text = it,
                style = MaterialTheme.typography.titleSmall,
                maxLines = if (isExpanded) Int.MAX_VALUE else 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isExpanded = !isExpanded }
                    .padding(horizontal = 16.dp)
                    .align(Alignment.CenterHorizontally)
                    .animateContentSize()
            )

            Spacer(modifier = Modifier.height(16.dp))
        }

        user.tags?.custom?.let {
            TagsRow(
                tags = it,
                onTagClicked = { query ->
                    navigateToSearch(query)
                },
                contentPadding = PaddingValues(horizontal = 16.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun UserStatsItem(
    @StringRes titleRes: Int,
    value: Long,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = stringResource(id = titleRes),
            style = MaterialTheme.typography.titleSmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

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
fun UserStatistics(
    user: User,
    modifier: Modifier = Modifier
) {
    // Pairs of (title, value)
    val statsGridItems = listOf(
        Pair(R.string.downloads, user.downloads),
        Pair(R.string.photos, user.totalPhotos),
        Pair(R.string.total_likes, user.totalLikes),
        Pair(R.string.collections, user.totalCollections),
        Pair(R.string.followers, user.followersCount),
        Pair(R.string.following, user.followingCount)
    )

    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier.height(90.dp)
    ) {
        items(statsGridItems) { item ->
            UserStatsItem(titleRes = item.first, value = item.second)
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
            social = UserSocialMediaLinks(
                instagramUsername = "abc",
                portfolioUrl = "abc",
                twitterUsername = "abc",
                paypalEmail = "abc"
            ),
            tags = UserTags(tags),
            photos = null
        )

        Surface {
            UserInfoBottomSheet(user = user, navigateToSearch = {})
        }
    }
}