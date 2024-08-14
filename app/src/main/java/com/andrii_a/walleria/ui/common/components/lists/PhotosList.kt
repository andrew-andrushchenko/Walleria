package com.andrii_a.walleria.ui.common.components.lists

import android.graphics.Bitmap
import android.graphics.drawable.ColorDrawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridScope
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toDrawable
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.andrii_a.walleria.R
import com.andrii_a.walleria.domain.PhotoQuality
import com.andrii_a.walleria.domain.models.photo.Photo
import com.andrii_a.walleria.domain.models.photo.PhotoUrls
import com.andrii_a.walleria.domain.models.user.User
import com.andrii_a.walleria.ui.common.PhotoId
import com.andrii_a.walleria.ui.common.UserNickname
import com.andrii_a.walleria.ui.common.components.EmptyContentBanner
import com.andrii_a.walleria.ui.common.components.ErrorBanner
import com.andrii_a.walleria.ui.common.components.ErrorItem
import com.andrii_a.walleria.ui.common.components.LoadingListItem
import com.andrii_a.walleria.ui.common.components.ScrollToTopLayout
import com.andrii_a.walleria.ui.theme.WalleriaTheme
import com.andrii_a.walleria.ui.util.BlurHashDecoder
import com.andrii_a.walleria.ui.util.getUrlByQuality
import com.andrii_a.walleria.ui.util.getUserProfileImageUrlOrEmpty
import com.andrii_a.walleria.ui.util.primaryColorInt
import com.andrii_a.walleria.ui.util.userFullName
import com.andrii_a.walleria.ui.util.userNickname
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun PhotosList(
    lazyPhotoItems: LazyPagingItems<Photo>,
    onPhotoClicked: (PhotoId) -> Unit,
    onUserProfileClicked: (UserNickname) -> Unit,
    modifier: Modifier = Modifier,
    isCompact: Boolean = false,
    addNavigationBarPadding: Boolean = false,
    photosLoadQuality: PhotoQuality = PhotoQuality.MEDIUM,
    listState: LazyListState = rememberLazyListState(),
    contentPadding: PaddingValues = PaddingValues(),
    headerContent: (@Composable () -> Unit)? = null
) {
    ScrollToTopLayout(
        listState = listState,
        scrollToTopButtonPadding = PaddingValues(
            bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding() +
                    dimensionResource(id = R.dimen.scroll_to_top_button_padding) +
                    if (addNavigationBarPadding) {
                        dimensionResource(id = R.dimen.navigation_bar_height)
                    } else {
                        0.dp
                    }
        ),
        modifier = modifier
    ) {
        if (lazyPhotoItems.itemCount < 0) {
            EmptyContentBanner(modifier = Modifier.fillMaxSize())
        } else {
            LazyColumn(
                state = listState,
                contentPadding = contentPadding
            ) {
                when (lazyPhotoItems.loadState.refresh) {
                    is LoadState.NotLoading -> {
                        loadedStateContent(
                            lazyPhotoItems = lazyPhotoItems,
                            headerContent = headerContent,
                            isItemCompact = isCompact,
                            photosLoadQuality = photosLoadQuality,
                            onPhotoClicked = onPhotoClicked,
                            onUserProfileClicked = onUserProfileClicked
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
                                onRetry = lazyPhotoItems::retry,
                                modifier = Modifier.fillParentMaxSize()
                            )
                        }
                    }
                }

                when (lazyPhotoItems.loadState.append) {
                    is LoadState.NotLoading -> Unit

                    is LoadState.Loading -> {
                        item {
                            LoadingListItem(modifier = Modifier.fillParentMaxWidth())
                        }
                    }

                    is LoadState.Error -> {
                        item {
                            ErrorItem(
                                onRetry = lazyPhotoItems::retry,
                                modifier = Modifier
                                    .fillParentMaxWidth()
                                    .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun LazyListScope.loadedStateContent(
    lazyPhotoItems: LazyPagingItems<Photo>,
    headerContent: (@Composable () -> Unit)? = null,
    isItemCompact: Boolean,
    photosLoadQuality: PhotoQuality,
    onPhotoClicked: (PhotoId) -> Unit,
    onUserProfileClicked: (UserNickname) -> Unit
) {
    headerContent?.let { header ->
        item {
            header.invoke()
        }
    }

    if (lazyPhotoItems.itemCount > 0) {
        items(
            count = lazyPhotoItems.itemCount,
            key = lazyPhotoItems.itemKey { it.id }
        ) { index ->
            val photo = lazyPhotoItems[index]
            photo?.let {
                if (isItemCompact) {
                    SimplePhotoItem(
                        photo = photo,
                        photosLoadQuality = photosLoadQuality,
                        onPhotoClicked = { onPhotoClicked(it.id) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                start = 16.dp,
                                end = 16.dp,
                                bottom = 16.dp
                            )
                    )
                } else {
                    DefaultPhotoItem(
                        photo = photo,
                        photosLoadQuality = photosLoadQuality,
                        onPhotoClicked = { onPhotoClicked(it.id) },
                        onUserClick = { onUserProfileClicked(photo.userNickname) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                start = 16.dp,
                                end = 16.dp,
                                bottom = 16.dp
                            )
                    )
                }
            }
        }
    } else {
        item {
            EmptyContentBanner(modifier = Modifier.fillParentMaxSize())
        }
    }
}

@Composable
fun PhotosGrid(
    lazyPhotoItems: LazyPagingItems<Photo>,
    onPhotoClicked: (PhotoId) -> Unit,
    modifier: Modifier = Modifier,
    addNavigationBarPadding: Boolean = false,
    photosLoadQuality: PhotoQuality = PhotoQuality.MEDIUM,
    gridState: LazyStaggeredGridState = rememberLazyStaggeredGridState(),
    contentPadding: PaddingValues = PaddingValues(),
    headerContent: (@Composable () -> Unit)? = null
) {
    ScrollToTopLayout(
        gridState = gridState,
        scrollToTopButtonPadding = PaddingValues(
            bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding() +
                    dimensionResource(id = R.dimen.scroll_to_top_button_padding) +
                    if (addNavigationBarPadding) {
                        dimensionResource(id = R.dimen.navigation_bar_height)
                    } else {
                        0.dp
                    }
        ),
        modifier = modifier
    ) {
        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Adaptive(150.dp),
            state = gridState,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalItemSpacing = 8.dp,
            contentPadding = contentPadding,
            modifier = Modifier.padding(horizontal = 8.dp)
        ) {
            when (lazyPhotoItems.loadState.refresh) {
                is LoadState.NotLoading -> {
                    loadedStateContent(
                        lazyPhotoItems = lazyPhotoItems,
                        headerContent = headerContent,
                        photosLoadQuality = photosLoadQuality,
                        onPhotoClicked = onPhotoClicked
                    )
                }

                is LoadState.Loading -> {
                    item(span = StaggeredGridItemSpan.FullLine) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }

                is LoadState.Error -> {
                    item(span = StaggeredGridItemSpan.FullLine) {
                        ErrorBanner(
                            onRetry = lazyPhotoItems::retry,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }

            when (lazyPhotoItems.loadState.append) {
                is LoadState.NotLoading -> Unit

                is LoadState.Loading -> {
                    item(span = StaggeredGridItemSpan.FullLine) {
                        LoadingListItem(modifier = Modifier.fillMaxWidth())
                    }
                }

                is LoadState.Error -> {
                    item(span = StaggeredGridItemSpan.FullLine) {
                        ErrorItem(
                            onRetry = lazyPhotoItems::retry,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                        )
                    }
                }
            }
        }
    }
}

private fun LazyStaggeredGridScope.loadedStateContent(
    lazyPhotoItems: LazyPagingItems<Photo>,
    headerContent: (@Composable () -> Unit)? = null,
    photosLoadQuality: PhotoQuality,
    onPhotoClicked: (PhotoId) -> Unit
) {
    headerContent?.let { header ->
        item(span = StaggeredGridItemSpan.FullLine) {
            header.invoke()
        }
    }

    if (lazyPhotoItems.itemCount > 0) {
        items(
            count = lazyPhotoItems.itemCount,
            key = lazyPhotoItems.itemKey { it.id }
        ) { index ->
            val photo = lazyPhotoItems[index]
            photo?.let {
                SimplePhotoItem(
                    photo = photo,
                    photosLoadQuality = photosLoadQuality,
                    onPhotoClicked = { onPhotoClicked(photo.id) },
                    shape = RoundedCornerShape(16.dp)
                )
            }
        }
    } else {
        item(span = StaggeredGridItemSpan.FullLine) {
            EmptyContentBanner(modifier = Modifier.fillMaxSize())
        }
    }
}

@Composable
fun SimplePhotoItem(
    photo: Photo,
    photosLoadQuality: PhotoQuality,
    onPhotoClicked: () -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(16.dp)
) {
    val context = LocalContext.current

    val placeholderBitmap by produceState<Bitmap?>(initialValue = null) {
        value = withContext(Dispatchers.Default) {
            BlurHashDecoder.decode(
                blurHash = photo.blurHash,
                width = 4,
                height = 3
            )
        }
    }

    AsyncImage(
        model = ImageRequest.Builder(context)
            .data(photo.getUrlByQuality(quality = photosLoadQuality))
            .crossfade(durationMillis = 1000)
            .placeholder(placeholderBitmap?.toDrawable(context.resources))
            .fallback(placeholderBitmap?.toDrawable(context.resources))
            .error(ColorDrawable(photo.primaryColorInt))
            .build(),
        contentScale = ContentScale.Crop,
        contentDescription = stringResource(id = R.string.photo),
        modifier = modifier
            .aspectRatio(photo.width.toFloat() / photo.height.toFloat())
            .fillMaxWidth()
            .clip(shape)
            .clickable(onClick = onPhotoClicked)
    )
}

@Composable
fun DefaultPhotoItem(
    photo: Photo,
    photosLoadQuality: PhotoQuality,
    onPhotoClicked: () -> Unit,
    onUserClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
    ) {
        UserRow(
            userProfileImageUrl = photo.getUserProfileImageUrlOrEmpty(),
            username = photo.userFullName,
            onUserClick = onUserClick
        )

        SimplePhotoItem(
            photo = photo,
            photosLoadQuality = photosLoadQuality,
            onPhotoClicked = onPhotoClicked
        )
    }
}

@Composable
fun UserRow(
    userProfileImageUrl: String,
    username: String,
    onUserClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(12.dp)
            .clickable(onClick = onUserClick)
    ) {
        val painter = rememberAsyncImagePainter(
            model = ImageRequest.Builder(LocalContext.current)
                .data(userProfileImageUrl)
                .crossfade(durationMillis = 1000)
                .placeholder(ColorDrawable(Color.Gray.toArgb()))
                .build(),
            contentScale = ContentScale.Fit
        )

        Image(
            painter = painter,
            contentDescription = stringResource(id = R.string.user_profile_image),
            modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
        )

        Text(
            text = username,
            style = MaterialTheme.typography.titleMedium,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Preview
@Composable
fun DefaultPhotoItemPreview() {
    WalleriaTheme {
        val user = User(
            id = "",
            username = "ABC",
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
            photos = null
        )

        val photo = Photo(
            id = "",
            width = 4000,
            height = 3000,
            color = "#E0E0E0",
            blurHash = "LFC\$yHwc8^\$yIAS\$%M%00KxukYIp",
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
            user = user
        )

        Surface {
            DefaultPhotoItem(
                photo = photo,
                photosLoadQuality = PhotoQuality.MEDIUM,
                onPhotoClicked = {},
                onUserClick = {}
            )
        }
    }
}
