package com.andrii_a.walleria.ui.common.components

import android.content.res.Configuration
import android.graphics.drawable.ColorDrawable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
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
import com.andrii_a.walleria.ui.theme.CloverShape
import com.andrii_a.walleria.ui.theme.WalleriaTheme
import com.andrii_a.walleria.ui.util.getPreviewPhotos
import com.andrii_a.walleria.ui.util.getProfileImageUrlOrEmpty
import com.andrii_a.walleria.ui.util.getUrlByQuality
import com.andrii_a.walleria.ui.util.userFullName

@Composable
fun UsersStaggeredGrid(
    lazyUserItems: LazyPagingItems<User>,
    modifier: Modifier = Modifier,
    onUserClick: (UserNickname) -> Unit,
    gridState: LazyStaggeredGridState = rememberLazyStaggeredGridState(),
    contentPadding: PaddingValues = PaddingValues()
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Adaptive(250.dp),
            state = gridState,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalItemSpacing = 8.dp,
            contentPadding = contentPadding,
            modifier = modifier
        ) {
            if (lazyUserItems.loadState.refresh is LoadState.NotLoading && lazyUserItems.itemCount > 0) {
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
            }

            when (lazyUserItems.loadState.append) {
                is LoadState.NotLoading -> Unit

                is LoadState.Loading -> {
                    item(span = StaggeredGridItemSpan.FullLine) {
                        LoadingListItem(modifier = Modifier.fillMaxWidth())
                    }
                }

                is LoadState.Error -> {
                    item(span = StaggeredGridItemSpan.FullLine) {
                        ErrorItem(
                            onRetry = lazyUserItems::retry,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                        )
                    }
                }
            }
        }

        if (lazyUserItems.loadState.refresh is LoadState.Error) {
            ErrorBanner(
                onRetry = lazyUserItems::retry,
                modifier = Modifier.fillMaxSize()
            )
        }

        if (lazyUserItems.loadState.refresh is LoadState.Loading) {
            LoadingListItem(modifier = Modifier.fillMaxSize())
        }

        val shouldShowEmptyContent = lazyUserItems.loadState.refresh is LoadState.NotLoading
                && lazyUserItems.itemCount == 0

        if (shouldShowEmptyContent) {
            EmptyContentBanner(modifier = Modifier.fillMaxSize())
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
        onClick = { onUserClick(user.username) },
        modifier = modifier
    ) {
        ConstraintLayout(modifier = Modifier.fillMaxWidth()) {
            val (
                profilePhoto, nicknameText, usernameText,
                photo0, photo1, photo2
            ) = createRefs()

            val previewPhotos = user.getPreviewPhotos()

            val placeholderColor = MaterialTheme.colorScheme.secondaryContainer

            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(user.getProfileImageUrlOrEmpty())
                    .crossfade(durationMillis = 1000)
                    .placeholder(ColorDrawable(placeholderColor.toArgb()))
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
                    .clip(CloverShape)
            )

            Text(
                text = user.userFullName,
                style = MaterialTheme.typography.titleMedium,
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
                    top.linkTo(usernameText.bottom, 4.dp)
                    end.linkTo(parent.end, 16.dp)
                    width = Dimension.fillToConstraints
                }
            )

            if (previewPhotos.isNotEmpty()) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(previewPhotos[0].getUrlByQuality(quality = PhotoQuality.LOW))
                        .crossfade(durationMillis = 1000)
                        .placeholder(ColorDrawable(placeholderColor.toArgb()))
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
                        .placeholder(ColorDrawable(placeholderColor.toArgb()))
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
                        .placeholder(ColorDrawable(placeholderColor.toArgb()))
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

@Composable
fun UsersGridContent(
    userItems: LazyPagingItems<User>,
    gridState: LazyStaggeredGridState = rememberLazyStaggeredGridState(),
    contentPadding: PaddingValues = PaddingValues(),
    scrollToTopButtonPadding: PaddingValues = PaddingValues(
        bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding() + 8.dp
    ),
    onUserClick: (UserNickname) -> Unit
) {
    ScrollToTopLayout(
        gridState = gridState,
        scrollToTopButtonPadding = scrollToTopButtonPadding
    ) {
        UsersStaggeredGrid(
            lazyUserItems = userItems,
            onUserClick = onUserClick,
            gridState = gridState,
            contentPadding = PaddingValues(
                top = contentPadding.calculateTopPadding() + 16.dp,
                bottom = contentPadding.calculateBottomPadding() + 150.dp,
                start = 16.dp,
                end = 16.dp,
            ),
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun DefaultUserItemPreview() {
    WalleriaTheme {
        val previewPhotos = (0..2).map {
            Photo(
                id = "id$it",
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
            )
        }

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