package com.andrii_a.walleria.ui.common.components

import android.graphics.Bitmap
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
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toDrawable
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.request.error
import coil3.request.fallback
import coil3.request.placeholder
import com.andrii_a.walleria.R
import com.andrii_a.walleria.domain.PhotoQuality
import com.andrii_a.walleria.domain.models.photo.Photo
import com.andrii_a.walleria.domain.models.photo.PhotoUrls
import com.andrii_a.walleria.domain.models.user.User
import com.andrii_a.walleria.ui.common.PhotoId
import com.andrii_a.walleria.ui.theme.CloverShape
import com.andrii_a.walleria.ui.theme.WalleriaTheme
import com.andrii_a.walleria.ui.util.BlurHashDecoder
import com.andrii_a.walleria.ui.util.getUrlByQuality
import com.andrii_a.walleria.ui.util.getUserProfileImageUrlOrEmpty
import com.andrii_a.walleria.ui.util.primaryColorInt
import com.andrii_a.walleria.ui.util.userFullName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID

@Composable
fun PhotosStaggeredGrid(
    lazyPhotoItems: LazyPagingItems<Photo>,
    onPhotoClicked: (PhotoId) -> Unit,
    modifier: Modifier = Modifier,
    photosLoadQuality: PhotoQuality = PhotoQuality.MEDIUM,
    gridState: LazyStaggeredGridState = rememberLazyStaggeredGridState(),
    contentPadding: PaddingValues = PaddingValues(),
    headerContent: (@Composable () -> Unit)? = null
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
            headerContent?.let { header ->
                item(span = StaggeredGridItemSpan.FullLine) {
                    header()
                }
            }

            if (lazyPhotoItems.loadState.refresh is LoadState.NotLoading && lazyPhotoItems.itemCount > 0) {
                items(
                    count = lazyPhotoItems.itemCount,
                    key = lazyPhotoItems.itemKey { it.id + UUID.randomUUID() }
                ) { index ->
                    val photo = lazyPhotoItems[index]
                    photo?.let {
                        DefaultPhotoItem(
                            photo = photo,
                            photosLoadQuality = photosLoadQuality,
                            onPhotoClicked = { onPhotoClicked(photo.id) },
                            modifier = Modifier.animateItem()
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

        if (lazyPhotoItems.loadState.refresh is LoadState.Error) {
            ErrorBanner(
                onRetry = lazyPhotoItems::retry,
                modifier = Modifier.fillMaxSize()
            )
        }

        if (lazyPhotoItems.loadState.refresh is LoadState.Loading) {
            LoadingListItem(modifier = Modifier.fillMaxSize())
        }

        val shouldShowEmptyContent = lazyPhotoItems.loadState.refresh is LoadState.NotLoading
                && lazyPhotoItems.itemCount == 0

        if (shouldShowEmptyContent) {
            EmptyContentBanner(modifier = Modifier.fillMaxSize())
        }
    }
}

@Composable
fun DefaultPhotoItem(
    photo: Photo,
    photosLoadQuality: PhotoQuality,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(16.dp),
    onPhotoClicked: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.secondaryContainer,
        tonalElevation = 3.dp,
        onClick = onPhotoClicked,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
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
                    .error(photo.primaryColorInt.toDrawable())
                    .build(),
                contentScale = ContentScale.Crop,
                contentDescription = stringResource(id = R.string.photo),
                modifier = modifier
                    .aspectRatio(photo.width.toFloat() / photo.height.toFloat())
                    .fillMaxWidth()
                    .clip(shape)
            )

            PhotoItemDetails(
                userProfileImageUrl = photo.getUserProfileImageUrlOrEmpty(),
                username = photo.userFullName,
                isSponsored = photo.sponsorship?.sponsor != null,
                modifier = Modifier.fillMaxWidth().padding()
            )
        }
    }
}

@Composable
private fun PhotoItemDetails(
    isSponsored: Boolean,
    userProfileImageUrl: String,
    username: String,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier
            .padding(12.dp)
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(userProfileImageUrl)
                .crossfade(durationMillis = 1000)
                .placeholder(Color.Gray.toArgb().toDrawable())
                .build(),
            contentScale = ContentScale.Fit,
            contentDescription = stringResource(id = R.string.user_profile_image),
            modifier = Modifier
                .size(36.dp)
                .clip(CloverShape)
        )

        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = username,
                style = MaterialTheme.typography.titleSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            if (isSponsored) {
                Text(
                    text = "Sponsored",
                    style = MaterialTheme.typography.labelSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

        }

    }
}

@Composable
fun PhotosGridContent(
    photoItems: LazyPagingItems<Photo>,
    modifier: Modifier = Modifier,
    gridState: LazyStaggeredGridState = rememberLazyStaggeredGridState(),
    contentPadding: PaddingValues = PaddingValues(),
    scrollToTopButtonPadding: PaddingValues = PaddingValues(
        bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding() + 8.dp
    ),
    onPhotoClicked: (PhotoId) -> Unit,
    headerContent: (@Composable () -> Unit)? = null
) {
    ScrollToTopLayout(
        gridState = gridState,
        scrollToTopButtonPadding = scrollToTopButtonPadding,
        modifier = modifier
    ) {
        PhotosStaggeredGrid(
            lazyPhotoItems = photoItems,
            onPhotoClicked = onPhotoClicked,
            gridState = gridState,
            contentPadding = PaddingValues(
                top = contentPadding.calculateTopPadding() + 16.dp,
                bottom = contentPadding.calculateBottomPadding() + 150.dp,
                start = 16.dp,
                end = 16.dp
            ),
            headerContent = headerContent,
            modifier = Modifier.fillMaxSize()
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
                onPhotoClicked = {}
            )
        }
    }
}
