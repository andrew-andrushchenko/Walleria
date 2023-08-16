package com.andrii_a.walleria.ui.user_details.components

import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.andrii_a.walleria.R
import com.andrii_a.walleria.domain.UserProfileImageQuality
import com.andrii_a.walleria.domain.models.user.User
import com.andrii_a.walleria.domain.models.user.UserSocialMediaLinks
import com.andrii_a.walleria.ui.theme.WalleriaTheme
import com.andrii_a.walleria.ui.util.getProfileImageUrlOrEmpty
import com.andrii_a.walleria.ui.util.openInstagramProfile
import com.andrii_a.walleria.ui.util.openLinkInBrowser
import com.andrii_a.walleria.ui.util.openTwitterProfile
import com.andrii_a.walleria.ui.util.userFullName

@Composable
private fun UserSocialMediaRow(userSocial: UserSocialMediaLinks) {
    val context = LocalContext.current

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        userSocial.portfolioUrl?.let {
            IconButton(onClick = { context.openLinkInBrowser(it) }) {
                Icon(
                    painterResource(id = R.drawable.ic_portfolio_outlined),
                    contentDescription = stringResource(id = R.string.portfolio_url)
                )
            }
        }

        Spacer(modifier = Modifier.padding(8.dp))

        userSocial.instagramUsername?.let {
            IconButton(onClick = { context.openInstagramProfile(it) }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_instagram_outlined),
                    contentDescription = stringResource(id = R.string.instagram_profile)
                )
            }
        }

        Spacer(modifier = Modifier.padding(8.dp))

        userSocial.twitterUsername?.let {
            IconButton(onClick = { context.openTwitterProfile(it) }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_twitter_x_outlined),
                    contentDescription = stringResource(id = R.string.x_twitter_profile)
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun UserHeader(
    user: User,
    modifier: Modifier = Modifier
) {
    Surface(modifier = modifier) {
        var openProfilePhotoViewDialog by rememberSaveable {
            mutableStateOf(false)
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(8.dp)
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(user.getProfileImageUrlOrEmpty(quality = UserProfileImageQuality.HIGH))
                    .crossfade(true)
                    .placeholder(ColorDrawable(Color.GRAY))
                    .build(),
                contentDescription = stringResource(id = R.string.user_profile_image),
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .combinedClickable(
                        onLongClick = { openProfilePhotoViewDialog = true },
                        onClick = {}
                    )
            )

            Text(
                text = user.userFullName,
                style = MaterialTheme.typography.titleLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            if (!user.location.isNullOrBlank()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_location),
                        contentDescription = stringResource(id = R.string.location_hint)
                    )

                    Text(
                        text = user.location,
                        style = MaterialTheme.typography.titleSmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            user.social?.let { userSocial ->
                UserSocialMediaRow(userSocial = userSocial)
            }
        }

        if (openProfilePhotoViewDialog) {
            Dialog(
                onDismissRequest = { openProfilePhotoViewDialog = false },
                properties = DialogProperties(
                    usePlatformDefaultWidth = false
                )
            ) {
                val configuration = LocalConfiguration.current

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.size((configuration.screenWidthDp / 2).dp)
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(user.getProfileImageUrlOrEmpty(quality = UserProfileImageQuality.HIGH))
                            .crossfade(true)
                            .placeholder(ColorDrawable(Color.GRAY))
                            .build(),
                        contentDescription = stringResource(id = R.string.user_profile_image),
                        modifier = Modifier.size((configuration.screenWidthDp / 2).dp)
                    )
                }

            }
        }

    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun UserHeaderPreview() {
    WalleriaTheme {
        val user = User(
            id = "",
            username = "ABC",
            firstName = "John",
            lastName = "Smith",
            bio = null,
            location = "San Francisco, California, USA",
            totalLikes = 0,
            totalPhotos = 0,
            totalCollections = 0,
            followersCount = 0,
            followingCount = 0,
            downloads = 0,
            profileImage = null,
            social = UserSocialMediaLinks(
                instagramUsername = "abc",
                portfolioUrl = "abc",
                twitterUsername = "abc",
                paypalEmail = "abc"
            ),
            tags = null,
            photos = null
        )

        UserHeader(user = user)
    }
}