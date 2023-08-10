package com.andrii_a.walleria.ui.common.components.lists

import android.graphics.drawable.ColorDrawable
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.Layout
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
import com.andrii_a.walleria.domain.models.collection.Collection
import com.andrii_a.walleria.domain.models.photo.Photo
import com.andrii_a.walleria.domain.models.photo.PhotoUrls
import com.andrii_a.walleria.ui.common.CollectionId
import com.andrii_a.walleria.ui.common.PhotoId
import com.andrii_a.walleria.ui.common.UserNickname
import com.andrii_a.walleria.ui.common.components.EmptyContentBanner
import com.andrii_a.walleria.ui.common.components.ErrorBanner
import com.andrii_a.walleria.ui.common.components.ErrorItem
import com.andrii_a.walleria.ui.common.components.LoadingListItem
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
    previewPhotosQuality: PhotoQuality = PhotoQuality.MEDIUM,
    listState: LazyListState = rememberLazyListState(),
    contentPadding: PaddingValues = PaddingValues()
) {
    LazyColumn(
        state = listState,
        contentPadding = contentPadding,
        modifier = modifier
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
                            val previewPhotos = remember {
                                collection.getPreviewPhotos()
                            }

                            val onPhotoClickListeners = remember {
                                previewPhotos.map { photo ->
                                    val listener: () -> Unit = {
                                        onPhotoClicked(PhotoId(photo.id))
                                    }
                                    listener
                                }
                            }

                            DefaultCollectionItem(
                                title = collection.title,
                                previewPhotos = previewPhotos,
                                totalPhotos = collection.totalPhotos,
                                previewPhotosQuality = previewPhotosQuality,
                                curatorUsername = collection.username,
                                onOpenCollectionClick = {
                                    onCollectionClicked(CollectionId(collection.id))
                                },
                                onUserProfileClick = {
                                    val userNickname = UserNickname(collection.username)
                                    onUserProfileClicked(userNickname)
                                },
                                onPhotoClickListeners = onPhotoClickListeners,
                                modifier = Modifier
                                    .padding(top = 8.dp, start = 16.dp, end = 16.dp, bottom = 48.dp)
                            )
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

@Composable
fun DefaultCollectionItem(
    title: String,
    previewPhotos: List<Photo>,
    previewPhotosQuality: PhotoQuality,
    totalPhotos: Long,
    curatorUsername: String,
    modifier: Modifier = Modifier,
    onOpenCollectionClick: () -> Unit,
    onUserProfileClick: () -> Unit,
    onPhotoClickListeners: List<() -> Unit>
) {
    require(previewPhotos.size <= 3) { "Requires at most 3 photos." }

    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier
    ) {
        PhotosGrid(
            previewPhotos = previewPhotos,
            previewPhotosQuality = previewPhotosQuality,
            onPhotoClickListeners = onPhotoClickListeners
        )

        DetailsRow(
            title = title,
            curatorUsername = curatorUsername,
            totalPhotos = totalPhotos,
            onUserProfileClick = onUserProfileClick,
            onOpenCollectionClick = onOpenCollectionClick,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun PhotosGrid(
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

@Preview
@Composable
fun PhotosGridPreview() {
    WalleriaTheme {
        PhotosGrid(
            previewPhotos = listOf(
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
                /*Photo(
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
                )*/
            ),
            previewPhotosQuality = PhotoQuality.MEDIUM,
            onPhotoClickListeners = listOf({}, {}, {}),
            //modifier = Modifier.padding(8.dp)
        )
    }
}

@Preview
@Composable
fun DetailsRowPreview() {
    WalleriaTheme {
        DetailsRow(
            title = "Title very very looooooooooooong title",
            curatorUsername = "John very very very long name Smith",
            totalPhotos = 100_000,
            onUserProfileClick = {},
            onOpenCollectionClick = {},
            modifier = Modifier.fillMaxWidth()
        )
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

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp)
    ) {
        Column(verticalArrangement = Arrangement.SpaceBetween) {


        }


    }
}