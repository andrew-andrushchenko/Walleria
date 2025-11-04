package com.andrii_a.walleria.ui.common.components

import android.graphics.Bitmap
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toDrawable
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.request.error
import coil3.request.placeholder
import com.andrii_a.walleria.R
import com.andrii_a.walleria.domain.PhotoQuality
import com.andrii_a.walleria.domain.models.collection.Collection
import com.andrii_a.walleria.domain.models.user.User
import com.andrii_a.walleria.ui.common.CollectionId
import com.andrii_a.walleria.ui.theme.WalleriaTheme
import com.andrii_a.walleria.ui.util.BlurHashDecoder
import com.andrii_a.walleria.ui.util.abbreviatedNumberString
import com.andrii_a.walleria.ui.util.getCoverPhotoUrl
import com.andrii_a.walleria.ui.util.primaryColorInt
import com.andrii_a.walleria.ui.util.username
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun CollectionsStaggeredGrid(
    lazyCollectionItems: LazyPagingItems<Collection>,
    onCollectionClick: (CollectionId) -> Unit,
    modifier: Modifier = Modifier,
    photosLoadQuality: PhotoQuality = PhotoQuality.MEDIUM,
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
            if (lazyCollectionItems.loadState.refresh is LoadState.NotLoading && lazyCollectionItems.itemCount > 0) {
                items(
                    count = lazyCollectionItems.itemCount,
                    key = lazyCollectionItems.itemKey { it.id }
                ) { index ->
                    val collection = lazyCollectionItems[index]
                    collection?.let {
                        DefaultCollectionItem(
                            collection = collection,
                            photoQuality = photosLoadQuality,
                            onCollectionClick = { onCollectionClick(collection.id) },
                            modifier = Modifier.animateItem()
                        )
                    }
                }
            }

            when (lazyCollectionItems.loadState.append) {
                is LoadState.NotLoading -> Unit

                is LoadState.Loading -> {
                    item(span = StaggeredGridItemSpan.FullLine) {
                        WLoadingIndicator(modifier = Modifier.fillMaxWidth())
                    }
                }

                is LoadState.Error -> {
                    item(span = StaggeredGridItemSpan.FullLine) {
                        ErrorItem(
                            onRetry = lazyCollectionItems::retry,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                        )
                    }
                }
            }
        }

        if (lazyCollectionItems.loadState.refresh is LoadState.Error) {
            ErrorBanner(
                onRetry = lazyCollectionItems::retry,
                modifier = Modifier.fillMaxSize()
            )
        }

        if (lazyCollectionItems.loadState.refresh is LoadState.Loading) {
            WLoadingIndicator(modifier = Modifier.fillMaxSize())
        }

        val shouldShowEmptyContent = lazyCollectionItems.loadState.refresh is LoadState.NotLoading
                && lazyCollectionItems.itemCount == 0

        if (shouldShowEmptyContent) {
            EmptyContentBanner(modifier = Modifier.fillMaxSize())
        }
    }
}

@Composable
private fun DefaultCollectionItem(
    collection: Collection,
    photoQuality: PhotoQuality,
    onCollectionClick: () -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(16.dp)
) {
    val resources = LocalResources.current

    Box(modifier = modifier.clickable(onClick = onCollectionClick)) {
        val placeholderBitmap by produceState<Bitmap?>(initialValue = null) {
            value = withContext(Dispatchers.Default) {
                BlurHashDecoder.decode(
                    blurHash = collection.coverPhoto?.blurHash,
                    width = 4,
                    height = 3
                )
            }
        }

        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(collection.getCoverPhotoUrl(quality = photoQuality))
                .crossfade(durationMillis = 1000)
                .placeholder(placeholderBitmap?.toDrawable(resources))
                .error((collection.coverPhoto?.primaryColorInt ?: Color.Gray.toArgb()).toDrawable())
                .build(),
            contentScale = ContentScale.Crop,
            contentDescription = stringResource(id = R.string.collection_cover_photo),
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(shape)
                .drawWithContent {
                    drawContent()
                    drawRect(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f))
                        )
                    )
                }
        )

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 16.dp, bottom = 16.dp, end = 16.dp)
        ) {
            Text(
                text = collection.title,
                style = MaterialTheme.typography.titleSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = stringResource(
                    id = R.string.bullet_template,
                    collection.username,
                    collection.totalPhotos.abbreviatedNumberString
                ),
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = Color.White
            )
        }
    }
}

@Composable
fun CollectionsGridContent(
    collectionItems: LazyPagingItems<Collection>,
    gridState: LazyStaggeredGridState = rememberLazyStaggeredGridState(),
    contentPadding: PaddingValues = PaddingValues(),
    scrollToTopButtonPadding: PaddingValues = WindowInsets.navigationBars.asPaddingValues(),
    scrollToTopButtonEnabled: Boolean = true,
    onCollectionClick: (CollectionId) -> Unit
) {
    val content = @Composable {
        CollectionsStaggeredGrid(
            lazyCollectionItems = collectionItems,
            onCollectionClick = onCollectionClick,
            gridState = gridState,
            contentPadding = PaddingValues(
                top = contentPadding.calculateTopPadding(),
                bottom = contentPadding.calculateBottomPadding() + 150.dp,
                start = contentPadding.calculateStartPadding(LayoutDirection.Ltr) + 8.dp,
                end = contentPadding.calculateEndPadding(LayoutDirection.Ltr) + 8.dp
            ),
            modifier = Modifier.fillMaxSize()
        )
    }

    if (scrollToTopButtonEnabled) {
        ScrollToTopLayout(
            gridState = gridState,
            scrollToTopButtonPadding = scrollToTopButtonPadding,
            grid = content
        )
    } else {
        content()
    }
}

@Preview
@Composable
fun DefaultCollectionItemPreview() {
    WalleriaTheme {
        val user = User(
            id = "",
            username = "ABC",
            firstName = "John",
            lastName = "Smith",
            bio = "",
            location = "",
            totalLikes = 100,
            totalPhotos = 100,
            totalCollections = 100,
            followersCount = 100_000,
            followingCount = 56,
            downloads = 99_000,
            profileImage = null,
            social = null,
            tags = null,
            photos = null
        )

        val collection = Collection(
            id = "",
            title = "Walleria",
            description = null,
            curated = false,
            featured = false,
            totalPhotos = 856_000,
            isPrivate = false,
            tags = null,
            coverPhoto = null,
            previewPhotos = null,
            links = null,
            user = user
        )

        DefaultCollectionItem(
            collection = collection,
            photoQuality = PhotoQuality.MEDIUM,
            onCollectionClick = {}
        )
    }
}

