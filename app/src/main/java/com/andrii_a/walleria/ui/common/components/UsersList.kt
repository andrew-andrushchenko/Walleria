package com.andrii_a.walleria.ui.common.components

import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.andrii_a.walleria.R
import com.andrii_a.walleria.domain.PhotoQuality
import com.andrii_a.walleria.domain.models.photo.Photo
import com.andrii_a.walleria.domain.models.photo.PhotoUrls
import com.andrii_a.walleria.domain.models.user.User
import com.andrii_a.walleria.ui.common.UserNickname
import com.andrii_a.walleria.ui.theme.WalleriaTheme
import com.andrii_a.walleria.ui.util.getPreviewPhotos
import com.andrii_a.walleria.ui.util.getProfileImageUrlOrEmpty
import com.andrii_a.walleria.ui.util.getUrlByQuality
import com.andrii_a.walleria.ui.util.primaryColorInt
import com.andrii_a.walleria.ui.util.userFullName

@Composable
fun UsersList(
    lazyUserItems: LazyPagingItems<User>,
    modifier: Modifier = Modifier,
    onUserClick: (UserNickname) -> Unit,
    listState: LazyListState = rememberLazyListState(),
    contentPadding: PaddingValues = PaddingValues()
) {
    ScrollToTopLayout(
        listState = listState,
        scrollToTopButtonPadding = PaddingValues(
            bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding() +
                    dimensionResource(id = R.dimen.scroll_to_top_button_padding)
        ),
        modifier = modifier
    ) {
        LazyColumn(
            state = listState,
            contentPadding = contentPadding
        ) {
            when (lazyUserItems.loadState.refresh) {
                is LoadState.NotLoading -> {
                    loadedStateContent(
                        lazyUserItems = lazyUserItems,
                        onUserClick = onUserClick
                    )
                }

                is LoadState.Loading -> {
                    item {
                        Box(
                            modifier = Modifier.fillParentMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }

                is LoadState.Error -> {
                    item {
                        ErrorBanner(
                            onRetry = lazyUserItems::retry,
                            modifier = Modifier.fillParentMaxSize()
                        )
                    }
                }
            }

            when (lazyUserItems.loadState.append) {
                is LoadState.NotLoading -> Unit

                is LoadState.Loading -> {
                    item {
                        LoadingListItem(modifier = Modifier.fillParentMaxWidth())
                    }
                }

                is LoadState.Error -> {
                    item {
                        ErrorItem(
                            onRetry = lazyUserItems::retry,
                            modifier = Modifier
                                .fillParentMaxWidth()
                                .padding(horizontal = 16.dp)
                                .padding(bottom = 16.dp)
                        )
                    }
                }
            }
        }
    }
}

private fun LazyListScope.loadedStateContent(
    lazyUserItems: LazyPagingItems<User>,
    onUserClick: (UserNickname) -> Unit
) {
    if (lazyUserItems.itemCount > 0) {
        items(
            count = lazyUserItems.itemCount,
            key = lazyUserItems.itemKey { it.id }
        ) { index ->
            val user = lazyUserItems[index]
            user?.let {
                DefaultUserItem(
                    user = it,
                    onUserClick = onUserClick,
                    modifier = Modifier.padding(
                        start = 16.dp,
                        end = 16.dp,
                        bottom = 16.dp
                    )
                )
            }
        }
    } else {
        item {
            EmptyContentBanner(modifier = Modifier.fillParentMaxSize())
        }
    }
}

@Composable
fun DefaultUserItem(
    user: User,
    modifier: Modifier = Modifier,
    onUserClick: (UserNickname) -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        onClick = {
            onUserClick(user.username)
        },
        modifier = modifier
    ) {
        ConstraintLayout(modifier = Modifier.fillMaxWidth()) {
            val (
                profilePhoto, nicknameText, usernameText,
                photo0, photo1, photo2
            ) = createRefs()

            val previewPhotos = user.getPreviewPhotos()

            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(user.getProfileImageUrlOrEmpty())
                    .crossfade(durationMillis = 1000)
                    .placeholder(ColorDrawable(Color.GRAY))
                    .build(),
                contentDescription = stringResource(id = R.string.user_profile_image),
                modifier = Modifier
                    .constrainAs(profilePhoto) {
                        top.linkTo(parent.top, 12.dp)
                        if (user
                                .getPreviewPhotos()
                                .isNotEmpty()
                        ) {
                            bottom.linkTo(photo0.top, 16.dp)
                        } else {
                            bottom.linkTo(parent.bottom, 16.dp)
                        }
                        start.linkTo(parent.start, 16.dp)
                    }
                    .size(64.dp)
                    .clip(CircleShape)
            )

            Text(
                text = user.userFullName,
                style = MaterialTheme.typography.titleLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.constrainAs(usernameText) {
                    start.linkTo(profilePhoto.end, 16.dp)
                    top.linkTo(profilePhoto.top, 8.dp)
                    end.linkTo(parent.end, 16.dp)
                    width = Dimension.fillToConstraints
                }
            )

            Text(
                text = stringResource(id = R.string.user_nickname_formatted, user.username),
                style = MaterialTheme.typography.titleSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.constrainAs(nicknameText) {
                    start.linkTo(profilePhoto.end, 16.dp)
                    top.linkTo(usernameText.bottom, 8.dp)
                    end.linkTo(parent.end, 16.dp)
                    width = Dimension.fillToConstraints
                }
            )

            if (previewPhotos.isNotEmpty()) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(previewPhotos[0].getUrlByQuality(quality = PhotoQuality.LOW))
                        .crossfade(durationMillis = 1000)
                        .placeholder(ColorDrawable(previewPhotos[0].primaryColorInt))
                        .build(),
                    contentScale = ContentScale.Crop,
                    contentDescription = null,
                    modifier = Modifier
                        .size(width = 100.dp, height = 120.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .constrainAs(photo0) {
                            top.linkTo(profilePhoto.bottom, 8.dp)
                            start.linkTo(parent.start, 12.dp)
                            end.linkTo(photo1.start)
                            bottom.linkTo(parent.bottom, 12.dp)
                        }
                )

                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(previewPhotos[1].getUrlByQuality(quality = PhotoQuality.LOW))
                        .crossfade(durationMillis = 1000)
                        .placeholder(ColorDrawable(previewPhotos[1].primaryColorInt))
                        .build(),
                    contentScale = ContentScale.Crop,
                    contentDescription = null,
                    modifier = Modifier
                        .size(width = 100.dp, height = 120.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .constrainAs(photo1) {
                            top.linkTo(photo0.top)
                            start.linkTo(photo0.end)
                            end.linkTo(photo2.start)
                            bottom.linkTo(photo0.bottom)
                        }
                )

                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(previewPhotos[2].getUrlByQuality(quality = PhotoQuality.LOW))
                        .crossfade(durationMillis = 1000)
                        .placeholder(ColorDrawable(previewPhotos[2].primaryColorInt))
                        .build(),
                    contentScale = ContentScale.Crop,
                    contentDescription = null,
                    modifier = Modifier
                        .size(width = 100.dp, height = 120.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .constrainAs(photo2) {
                            top.linkTo(photo1.top)
                            start.linkTo(photo1.end)
                            end.linkTo(parent.end, 12.dp)
                            bottom.linkTo(photo1.bottom)
                        }
                )
            }
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun DefaultUserItemPreview() {
    WalleriaTheme {
        val previewPhotos = listOf(
            Photo(
                id = "",
                width = 200,
                height = 300,
                color = "#E0E0E0",
                blurHash = "",
                views = 200,
                downloads = 200,
                likes = 10,
                likedByUser = false,
                description = "",
                exif = null,
                location = null,
                tags = null,
                relatedCollections = null,
                currentUserCollections = null,
                sponsorship = null,
                urls = PhotoUrls("", "", "", "", ""),
                links = null,
                user = null
            ),
            Photo(
                id = "",
                width = 200,
                height = 300,
                blurHash = "",
                color = "#E0E0E0",
                views = 200,
                downloads = 200,
                likes = 10,
                likedByUser = false,
                description = "",
                exif = null,
                location = null,
                tags = null,
                relatedCollections = null,
                currentUserCollections = null,
                sponsorship = null,
                urls = PhotoUrls("", "", "", "", ""),
                links = null,
                user = null
            ),
            Photo(
                id = "",
                width = 200,
                height = 300,
                blurHash = "",
                views = 200,
                downloads = 200,
                likes = 10,
                color = "#E0E0E0",
                likedByUser = false,
                description = "",
                exif = null,
                location = null,
                tags = null,
                relatedCollections = null,
                currentUserCollections = null,
                sponsorship = null,
                urls = PhotoUrls("", "", "", "", ""),
                links = null,
                user = null
            )
        )

        val user = User(
            id = "",
            username = "johny_smith",
            firstName = "John",
            lastName = "Smith",
            bio = null,
            location = null,
            totalLikes = 0,
            totalPhotos = 0,
            totalCollections = 0,
            followersCount = 0,
            followingCount = 0,
            downloads = 0,
            profileImage = null,
            social = null,
            tags = null,
            photos = previewPhotos
        )

        DefaultUserItem(
            user = user,
            onUserClick = {}
        )
    }
}