package com.andrii_a.walleria.ui.user_details.components

import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.andrii_a.walleria.R
import com.andrii_a.walleria.domain.UserProfileImageQuality
import com.andrii_a.walleria.domain.models.user.User
import com.andrii_a.walleria.domain.models.user.UserSocialMediaLinks
import com.andrii_a.walleria.ui.common.UserNickname
import com.andrii_a.walleria.ui.theme.WalleriaTheme
import com.andrii_a.walleria.ui.util.getProfileImageUrlOrEmpty
import com.andrii_a.walleria.ui.util.openInstagramProfile
import com.andrii_a.walleria.ui.util.openLinkInBrowser
import com.andrii_a.walleria.ui.util.openTwitterProfile
import com.andrii_a.walleria.ui.util.openUserProfileInBrowser
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
                style = MaterialTheme.typography.h6,
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
                        style = MaterialTheme.typography.subtitle2,
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

@Composable
fun UserDetailsTopBar(
    modifier: Modifier = Modifier,
    titleText: String? = null,
    isOwnProfile: Boolean = false,
    onNavigateBack: () -> Unit,
    onEditProfile: (() -> Unit)? = null,
    onOpenMoreAboutProfile: (() -> Unit)? = null
) {
    val context = LocalContext.current

    Surface(
        modifier = modifier.height(
            dimensionResource(id = R.dimen.top_bar_height) +
                    WindowInsets.systemBars
                        .asPaddingValues()
                        .calculateTopPadding()
        )
    ) {
        ConstraintLayout(
            modifier = Modifier.fillMaxWidth()
        ) {
            val (backButton, title, editButton, dropdownMenuBox) = createRefs()

            IconButton(
                onClick = onNavigateBack,
                modifier = Modifier
                    .constrainAs(backButton) {
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start, 8.dp)
                        if (!titleText.isNullOrBlank()) {
                            end.linkTo(title.start)
                        }
                    }
                    .statusBarsPadding()
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_arrow_back),
                    contentDescription = stringResource(id = R.string.navigate_back)
                )
            }

            titleText?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.h6,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .constrainAs(title) {
                            top.linkTo(backButton.top)
                            bottom.linkTo(backButton.bottom)
                            start.linkTo(backButton.end, 16.dp)
                            if (isOwnProfile) {
                                end.linkTo(editButton.start)
                            } else {
                                end.linkTo(dropdownMenuBox.end)
                            }
                            width = Dimension.fillToConstraints
                        }
                        .statusBarsPadding()
                )
            }

            if (isOwnProfile) {
                IconButton(
                    onClick = { onEditProfile?.invoke() },
                    modifier = Modifier
                        .constrainAs(editButton) {
                            top.linkTo(backButton.top)
                            bottom.linkTo(backButton.bottom)
                            end.linkTo(dropdownMenuBox.start)
                        }
                        .statusBarsPadding()
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_edit_outlined),
                        contentDescription = stringResource(id = R.string.edit_collection)
                    )
                }
            }

            Box(
                modifier = Modifier
                    .constrainAs(dropdownMenuBox) {
                        top.linkTo(backButton.top)
                        bottom.linkTo(backButton.bottom)
                        end.linkTo(parent.end, 8.dp)
                    }
                    .statusBarsPadding()
            ) {
                var menuExpanded by rememberSaveable {
                    mutableStateOf(false)
                }

                IconButton(
                    onClick = { menuExpanded = !menuExpanded }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_more_outlined),
                        contentDescription = stringResource(id = R.string.edit_collection)
                    )
                }

                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false }
                ) {
                    DropdownMenuItem(
                        onClick = {
                            titleText?.let {
                                context.openUserProfileInBrowser(UserNickname(it))
                            }
                        },
                    ) {
                        Text(text = stringResource(id = R.string.open_in_browser))
                    }

                    DropdownMenuItem(
                        onClick = { onOpenMoreAboutProfile?.invoke() },
                    ) {
                        Text(text = stringResource(id = R.string.more_about_profile))
                    }
                }
            }
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun UserDetailsTopBarPreview() {
    WalleriaTheme {
        UserDetailsTopBar(
            titleText = "user_nickname",
            isOwnProfile = true,
            onNavigateBack = {},
            onEditProfile = {},
        )
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