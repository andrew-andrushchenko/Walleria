package com.andrii_a.walleria.ui.common.components.lists

import android.graphics.drawable.ColorDrawable
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.Layout
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
import com.andrii_a.walleria.domain.models.collection.Collection
import com.andrii_a.walleria.domain.models.photo.Photo
import com.andrii_a.walleria.domain.models.user.User
import com.andrii_a.walleria.ui.common.CollectionId
import com.andrii_a.walleria.ui.common.PhotoId
import com.andrii_a.walleria.ui.common.UserNickname
import com.andrii_a.walleria.ui.common.components.EmptyContentBanner
import com.andrii_a.walleria.ui.common.components.ErrorBanner
import com.andrii_a.walleria.ui.common.components.ErrorItem
import com.andrii_a.walleria.ui.common.components.LoadingListItem
import com.andrii_a.walleria.ui.common.components.ScrollToTopLayout
import com.andrii_a.walleria.ui.theme.PrimaryDark
import com.andrii_a.walleria.ui.theme.PrimaryLight
import com.andrii_a.walleria.ui.theme.WalleriaTheme
import com.andrii_a.walleria.ui.util.abbreviatedNumberString
import com.andrii_a.walleria.ui.util.getPreviewPhotos
import com.andrii_a.walleria.ui.util.getUrlByQuality
import com.andrii_a.walleria.ui.util.primaryColorInt
import com.andrii_a.walleria.ui.util.username

@Composable
fun CollectionsList(
    lazyCollectionItems: LazyPagingItems<Collection>,
    onCollectionClicked: (CollectionId) -> Unit,
    onUserProfileClicked: (UserNickname) -> Unit,
    onPhotoClicked: (PhotoId) -> Unit,
    modifier: Modifier = Modifier,
    isCompact: Boolean = false,
    addNavBarPadding: Boolean = false,
    previewPhotosQuality: PhotoQuality = PhotoQuality.MEDIUM,
    listState: LazyListState = rememberLazyListState(),
    contentPadding: PaddingValues = PaddingValues()
) {
    ScrollToTopLayout(
        listState = listState,
        contentPadding = PaddingValues(
            bottom = WindowInsets.navigationBars
                .asPaddingValues()
                .calculateBottomPadding()
                    + dimensionResource(id = R.dimen.scroll_to_top_button_padding)
                    + if (addNavBarPadding) dimensionResource(id = R.dimen.navigation_bar_height) else 0.dp
        ),
        modifier = modifier
    ) {
        LazyColumn(
            state = listState,
            contentPadding = contentPadding
        ) {
            when (lazyCollectionItems.loadState.refresh) {
                is LoadState.NotLoading -> {
                    if (lazyCollectionItems.itemCount > 0) {
                        items(
                            count = lazyCollectionItems.itemCount,
                            key = lazyCollectionItems.itemKey { it.id }
                        ) { index ->
                            val collection = lazyCollectionItems[index]
                            collection?.let {
                                if (isCompact) {
                                    SimpleCollectionItem(
                                        collection = collection,
                                        photoQuality = previewPhotosQuality,
                                        onOpenCollectionClick = {
                                            onCollectionClicked(CollectionId(collection.id))
                                        },
                                        modifier = Modifier.padding(
                                            start = 16.dp,
                                            end = 16.dp,
                                            bottom = 16.dp
                                        )
                                    )
                                } else {
                                    DefaultCollectionItem(
                                        collection = collection,
                                        photoQuality = previewPhotosQuality,
                                        onPhotoClicked = onPhotoClicked,
                                        onOpenCollectionClick = {
                                            onCollectionClicked(CollectionId(collection.id))
                                        },
                                        onUserProfileClick = {
                                            val userNickname = UserNickname(collection.username)
                                            onUserProfileClicked(userNickname)
                                        },
                                        modifier = Modifier
                                            .padding(
                                                top = 8.dp,
                                                start = 16.dp,
                                                end = 16.dp,
                                                bottom = 48.dp
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

                is LoadState.Loading -> Unit

                is LoadState.Error -> {
                    item {
                        ErrorBanner(
                            onRetry = lazyCollectionItems::retry,
                            modifier = Modifier.fillParentMaxSize()
                        )
                    }
                }
            }

            when (lazyCollectionItems.loadState.append) {
                is LoadState.NotLoading -> Unit

                is LoadState.Loading -> {
                    item {
                        LoadingListItem(modifier = Modifier.fillParentMaxWidth())
                    }
                }

                is LoadState.Error -> {
                    item {
                        ErrorItem(
                            onRetry = lazyCollectionItems::retry,
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

@Composable
fun CollectionsGrid(
    lazyCollectionItems: LazyPagingItems<Collection>,
    onCollectionClicked: (CollectionId) -> Unit,
    modifier: Modifier = Modifier,
    addNavBarPadding: Boolean = false,
    coverPhotoQuality: PhotoQuality = PhotoQuality.MEDIUM,
    gridState: LazyGridState,
    contentPadding: PaddingValues = PaddingValues()
) {
    ScrollToTopLayout(
        gridState = gridState,
        contentPadding = PaddingValues(
            bottom = WindowInsets.navigationBars
                .asPaddingValues()
                .calculateBottomPadding()
                    + dimensionResource(id = R.dimen.scroll_to_top_button_padding)
                    + if (addNavBarPadding) dimensionResource(id = R.dimen.navigation_bar_height) else 0.dp
        ),
        modifier = modifier
    )  {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = contentPadding,
            modifier = Modifier.padding(horizontal = 8.dp)
        ) {
            when (lazyCollectionItems.loadState.refresh) {
                is LoadState.NotLoading -> {
                    if (lazyCollectionItems.itemCount > 0) {
                        items(
                            count = lazyCollectionItems.itemCount,
                            key = lazyCollectionItems.itemKey { it.id }
                        ) { index ->
                            val collection = lazyCollectionItems[index]
                            collection?.let {
                                SimpleCollectionItem(
                                    collection = collection,
                                    photoQuality = coverPhotoQuality,
                                    onOpenCollectionClick = {
                                        onCollectionClicked(CollectionId(collection.id))
                                    }
                                )
                            }
                        }
                    } else {
                        item(span = { GridItemSpan(2) }) {
                            EmptyContentBanner(modifier = Modifier.fillMaxSize())
                        }
                    }
                }

                is LoadState.Loading -> Unit

                is LoadState.Error -> {
                    item(span = { GridItemSpan(2) }) {
                        ErrorBanner(
                            onRetry = lazyCollectionItems::retry,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }

            when (lazyCollectionItems.loadState.append) {
                is LoadState.NotLoading -> Unit

                is LoadState.Loading -> {
                    item(span = { GridItemSpan(2) }) {
                        LoadingListItem(modifier = Modifier.fillMaxWidth())
                    }
                }

                is LoadState.Error -> {
                    item(span = { GridItemSpan(2) }) {
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
    }
}

@Composable
private fun DefaultCollectionItem(
    collection: Collection,
    photoQuality: PhotoQuality,
    modifier: Modifier = Modifier,
    onPhotoClicked: (PhotoId) -> Unit,
    onOpenCollectionClick: () -> Unit,
    onUserProfileClick: () -> Unit,
) {
    val previewPhotos = remember {
        collection.getPreviewPhotos(quality = photoQuality)
    }

    val onPhotoClickListeners = remember {
        previewPhotos.map { photo ->
            val listener: () -> Unit = {
                onPhotoClicked(PhotoId(photo.id))
            }
            listener
        }
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier
    ) {
        PhotoTiles(
            previewPhotos = previewPhotos,
            previewPhotosQuality = photoQuality,
            onPhotoClickListeners = onPhotoClickListeners
        )

        DetailsRow(
            title = collection.title,
            curatorUsername = collection.username,
            totalPhotos = collection.totalPhotos,
            onUserProfileClick = onUserProfileClick,
            onOpenCollectionClick = onOpenCollectionClick,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun PhotoTiles(
    previewPhotos: List<Photo>,
    previewPhotosQuality: PhotoQuality,
    onPhotoClickListeners: List<() -> Unit>,
    modifier: Modifier = Modifier
) {
    Layout(
        content = {
            require(previewPhotos.size <= 3) { "Requires at most 3 photos for the grid." }

            previewPhotos.forEachIndexed { index, photo ->
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(photo.getUrlByQuality(previewPhotosQuality))
                        .crossfade(durationMillis = 1000)
                        .placeholder(ColorDrawable(photo.primaryColorInt))
                        .build(),
                    contentDescription = stringResource(id = R.string.description_first_photo),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .clickable(onClick = onPhotoClickListeners[index])
                )
            }
        },
        modifier = modifier
            .fillMaxWidth()
            .wrapContentWidth(Alignment.CenterHorizontally)
    ) { measurables, constraints ->
        val padding = 8.dp.roundToPx()
        // to cover small screens
        val minDimension = minOf(constraints.maxHeight, constraints.maxWidth)

        when (measurables.size) {
            1 -> {
                val placeable = measurables[0].measure(constraints)
                // calculate size of the layout
                val height = placeable.height
                val width = constraints.maxWidth

                layout(width, height) {
                    placeable.place(0, 0)
                }
            }

            2 -> {
                val smallImageConstraints = constraints.copy(
                    minWidth = (minDimension - padding * 2) / 2,
                    maxWidth = (minDimension - padding * 2) / 2
                )

                val placeables = measurables.map {
                    it.measure(smallImageConstraints)
                }

                // calculate size of the layout
                val height = placeables[0].height
                val width = placeables[0].width * 2 + padding * 2

                layout(width, height) {
                    var positionX = 0

                    placeables.forEach { placeable ->
                        placeable.place(positionX, 0)
                        positionX += placeable.width + padding
                    }
                }
            }

            3 -> {
                val smallImageConstraints = constraints.copy(
                    minWidth = (minDimension - padding * 2) / 3,
                    maxWidth = (minDimension - padding * 2) / 3
                )

                val placeables = measurables
                    .subList(fromIndex = 1, toIndex = measurables.size)
                    .map {
                        it.measure(smallImageConstraints)
                    }

                val bigImageConstraints = constraints.copy(
                    minWidth = minDimension - padding - placeables[0].width,
                    maxWidth = minDimension - padding - placeables[0].width
                )

                val bigImagePlaceable = measurables.first().measure(bigImageConstraints)

                // calculate size of the layout
                val height = placeables[0].height * 2 + padding * 2
                val width = placeables[0].width * 3 + padding * 2

                layout(width, height) {
                    var positionY = 0

                    bigImagePlaceable.place(0, positionY)

                    placeables.forEach { placeable ->
                        // to the right from the big image
                        placeable.place(bigImagePlaceable.width + padding, positionY)
                        positionY += placeable.height + padding
                    }
                }
            }

            else -> throw IllegalStateException("Requires at most 3 photos for the grid.")
        }
    }
}

@Composable
private fun DetailsRow(
    title: String,
    curatorUsername: String,
    totalPhotos: Long,
    onUserProfileClick: () -> Unit,
    onOpenCollectionClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ConstraintLayout(modifier = modifier) {
        val (titleText, infoText, openButton) = createRefs()

        Text(
            text = title,
            style = MaterialTheme.typography.h6,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.constrainAs(titleText) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
                end.linkTo(openButton.start, 8.dp)
                width = Dimension.fillToConstraints
            }
        )

        Text(
            text = stringResource(
                id = R.string.bullet_template,
                curatorUsername,
                totalPhotos.abbreviatedNumberString
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .constrainAs(infoText) {
                    top.linkTo(titleText.bottom, 4.dp)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(titleText.start)
                    end.linkTo(openButton.start, 8.dp)
                    width = Dimension.fillToConstraints
                }
                .clickable(onClick = onUserProfileClick)
        )

        FloatingActionButton(
            onClick = onOpenCollectionClick,
            shape = RoundedCornerShape(16.dp),
            content = {
                Icon(
                    imageVector = Icons.Default.ArrowForwardIos,
                    contentDescription = stringResource(id = R.string.description_open_collection)
                )
            },
            modifier = Modifier.constrainAs(openButton) {
                top.linkTo(titleText.top)
                bottom.linkTo(infoText.bottom)
                end.linkTo(parent.end, 4.dp)
                start.linkTo(infoText.end)
            }
        )
    }
}

@Composable
private fun SimpleCollectionItem(
    collection: Collection,
    photoQuality: PhotoQuality,
    onOpenCollectionClick: () -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(16.dp)
) {
    Box(modifier = modifier.clickable(onClick = onOpenCollectionClick)) {
        AsyncImage(
            model = ImageRequest
                .Builder(LocalContext.current)
                .data(collection.coverPhoto?.getUrlByQuality(photoQuality))
                .crossfade(durationMillis = 1000)
                .placeholder(ColorDrawable(collection.coverPhoto?.primaryColorInt ?: android.graphics.Color.GRAY))
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
                            colors = listOf(Color.Transparent, PrimaryDark.copy(alpha = 0.7f))
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
                style = MaterialTheme.typography.subtitle1,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = PrimaryLight
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = stringResource(
                    id = R.string.bullet_template,
                    collection.username,
                    collection.totalPhotos.abbreviatedNumberString
                ),
                style = MaterialTheme.typography.caption,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = PrimaryLight
            )
        }
    }
}

@Preview
@Composable
fun SimpleCollectionItemPreview() {
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

        SimpleCollectionItem(
            collection = collection,
            photoQuality = PhotoQuality.MEDIUM,
            onOpenCollectionClick = {}
        )
    }
}

