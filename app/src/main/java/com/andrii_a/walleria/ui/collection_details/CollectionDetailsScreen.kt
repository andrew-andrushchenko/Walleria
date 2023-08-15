package com.andrii_a.walleria.ui.collection_details

import android.graphics.drawable.ColorDrawable
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Text
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.andrii_a.walleria.R
import com.andrii_a.walleria.domain.PhotoQuality
import com.andrii_a.walleria.domain.PhotosListLayoutType
import com.andrii_a.walleria.domain.models.collection.Collection
import com.andrii_a.walleria.domain.models.photo.Photo
import com.andrii_a.walleria.ui.common.PhotoId
import com.andrii_a.walleria.ui.common.UserNickname
import com.andrii_a.walleria.ui.common.components.ErrorBanner
import com.andrii_a.walleria.ui.common.components.LoadingBanner
import com.andrii_a.walleria.ui.common.components.lists.PhotosGrid
import com.andrii_a.walleria.ui.common.components.lists.PhotosList
import com.andrii_a.walleria.ui.theme.PrimaryDark
import com.andrii_a.walleria.ui.util.getUrlByQuality
import com.andrii_a.walleria.ui.util.username
import kotlinx.coroutines.launch

@Composable
fun CollectionDetailsScreen(
    loadResult: CollectionLoadResult,
    loggedInUsername: UserNickname,
    photosListLayoutType: PhotosListLayoutType,
    photosLoadQuality: PhotoQuality,
    onEvent: (CollectionDetailsEvent) -> Unit,
    navigateBack: () -> Unit,
    navigateToPhotoDetails: (PhotoId) -> Unit,
    navigateToUserDetails: (UserNickname) -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        when (loadResult) {
            is CollectionLoadResult.Empty -> Unit
            is CollectionLoadResult.Loading -> {
                LoadingStateContent(
                    onNavigateBack = navigateBack,
                    modifier = Modifier
                        .fillMaxSize()
                        .statusBarsPadding()
                )
            }

            is CollectionLoadResult.Error -> {
                ErrorStateContent(
                    onRetry = {
                        onEvent(CollectionDetailsEvent.RequestCollection(loadResult.collectionId))
                    },
                    onNavigateBack = navigateBack,
                    modifier = Modifier
                        .fillMaxSize()
                        .statusBarsPadding()
                )
            }

            is CollectionLoadResult.Success -> {
                SuccessStateContent(
                    collection = loadResult.collection,
                    collectionPhotosLazyItems = loadResult.collectionPhotos.collectAsLazyPagingItems(),
                    photosListLayoutType = photosListLayoutType,
                    photosLoadQuality = photosLoadQuality,
                    onEvent = onEvent,
                    loggedInUsername = loggedInUsername,
                    navigateToPhotoDetails = navigateToPhotoDetails,
                    navigateToUserDetails = navigateToUserDetails,
                    navigateBack = navigateBack,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Composable
private fun TopBar(
    modifier: Modifier = Modifier,
    titleText: String? = null,
    isOwnCollection: Boolean = false,
    onNavigateBack: () -> Unit,
    onEditCollection: (() -> Unit)? = null
) {
    ConstraintLayout(
        modifier = modifier
            .height(
                dimensionResource(id = R.dimen.top_bar_height) +
                        WindowInsets.systemBars
                            .asPaddingValues()
                            .calculateTopPadding()
            )
            .fillMaxWidth()
    ) {
        val (backButton, title, editButton) = createRefs()

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
                        if (isOwnCollection) {
                            end.linkTo(editButton.start)
                        } else {
                            end.linkTo(parent.end, 8.dp)
                        }
                        width = Dimension.fillToConstraints
                    }
                    .statusBarsPadding()
            )
        }

        if (isOwnCollection) {
            IconButton(
                onClick = { onEditCollection?.invoke() },
                modifier = Modifier
                    .constrainAs(editButton) {
                        top.linkTo(title.top)
                        bottom.linkTo(title.bottom)
                        end.linkTo(parent.end, 8.dp)
                    }
                    .statusBarsPadding()
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_edit_outlined),
                    contentDescription = stringResource(id = R.string.edit_collection)
                )
            }
        }
    }
}

@Composable
private fun LoadingStateContent(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        LoadingBanner(
            modifier = Modifier
                .fillMaxSize()
                .background(PrimaryDark.copy(alpha = 0.4f))
        )

        TopBar(
            onNavigateBack = onNavigateBack,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
        )
    }
}

@Composable
private fun ErrorStateContent(
    onRetry: () -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        ErrorBanner(
            onRetry = onRetry,
            modifier = Modifier.fillMaxSize()
        )

        TopBar(
            onNavigateBack = onNavigateBack,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .padding(8.dp)
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun SuccessStateContent(
    collection: Collection,
    onEvent: (CollectionDetailsEvent) -> Unit,
    collectionPhotosLazyItems: LazyPagingItems<Photo>,
    photosListLayoutType: PhotosListLayoutType,
    photosLoadQuality: PhotoQuality,
    loggedInUsername: UserNickname,
    navigateToPhotoDetails: (PhotoId) -> Unit,
    navigateToUserDetails: (UserNickname) -> Unit,
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    val modalBottomSheetState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)

    ModalBottomSheetLayout(
        sheetContent = {
            EditCollectionInfoBottomSheet(
                collection = collection,
                onEvent = onEvent,
                onDismiss = {
                    scope.launch {
                        modalBottomSheetState.hide()
                    }
                }
            )
        },
        sheetState = modalBottomSheetState,
        sheetShape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        val pullRefreshState = rememberPullRefreshState(
            refreshing = collectionPhotosLazyItems.loadState.refresh is LoadState.Loading,
            onRefresh = collectionPhotosLazyItems::refresh,
            refreshingOffset = dimensionResource(id = R.dimen.top_bar_height) + WindowInsets.systemBars.asPaddingValues()
                .calculateTopPadding()
        )

        Box(modifier = modifier.pullRefresh(pullRefreshState)) {
            val colorPrimaryTransparent = MaterialTheme.colors.primary.copy(alpha = 0.4f)
            val colorPrimary = MaterialTheme.colors.primary

            AsyncImage(
                model = ImageRequest
                    .Builder(LocalContext.current)
                    .data(collection.coverPhoto?.getUrlByQuality(quality = PhotoQuality.MEDIUM))
                    .crossfade(durationMillis = 1000)
                    .placeholder(ColorDrawable(Color.Gray.toArgb()))
                    .build(),
                contentScale = ContentScale.Crop,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp)
                    .drawWithContent {
                        drawContent()
                        drawRect(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    colorPrimaryTransparent,
                                    colorPrimary
                                )
                            )
                        )
                    }
            )

            val listState = rememberLazyListState()
            val gridState = rememberLazyStaggeredGridState()

            when (photosListLayoutType) {
                PhotosListLayoutType.DEFAULT -> {
                    PhotosList(
                        lazyPhotoItems = collectionPhotosLazyItems,
                        onPhotoClicked = navigateToPhotoDetails,
                        onUserProfileClicked = navigateToUserDetails,
                        headerContent = {
                            CollectionDescriptionHeader(
                                owner = collection.user,
                                description = collection.description,
                                totalPhotos = collection.totalPhotos,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 24.dp, vertical = 16.dp)
                            )
                        },
                        isCompact = false,
                        photosLoadQuality = photosLoadQuality,
                        listState = listState,
                        contentPadding = PaddingValues(
                            top = WindowInsets.systemBars.asPaddingValues()
                                .calculateTopPadding() + dimensionResource(id = R.dimen.top_bar_height),
                            bottom = WindowInsets.navigationBars.asPaddingValues()
                                .calculateBottomPadding() + 200.dp
                        ),
                        modifier = Modifier.fillMaxSize()
                    )
                }

                PhotosListLayoutType.MINIMAL_LIST -> {
                    PhotosList(
                        lazyPhotoItems = collectionPhotosLazyItems,
                        onPhotoClicked = navigateToPhotoDetails,
                        onUserProfileClicked = navigateToUserDetails,
                        headerContent = {
                            CollectionDescriptionHeader(
                                owner = collection.user,
                                description = collection.description,
                                totalPhotos = collection.totalPhotos,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 24.dp, vertical = 16.dp)
                            )
                        },
                        isCompact = true,
                        photosLoadQuality = photosLoadQuality,
                        listState = listState,
                        contentPadding = PaddingValues(
                            top = WindowInsets.systemBars.asPaddingValues()
                                .calculateTopPadding() + dimensionResource(id = R.dimen.top_bar_height),
                            bottom = WindowInsets.navigationBars.asPaddingValues()
                                .calculateBottomPadding() + 200.dp
                        ),
                        modifier = Modifier.fillMaxSize()
                    )
                }

                PhotosListLayoutType.STAGGERED_GRID -> {
                    PhotosGrid(
                        lazyPhotoItems = collectionPhotosLazyItems,
                        onPhotoClicked = navigateToPhotoDetails,
                        photosLoadQuality = photosLoadQuality,
                        headerContent = {
                            CollectionDescriptionHeader(
                                owner = collection.user,
                                description = collection.description,
                                totalPhotos = collection.totalPhotos,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 24.dp, vertical = 16.dp)
                            )
                        },
                        gridState = gridState,
                        contentPadding = PaddingValues(
                            top = WindowInsets.systemBars.asPaddingValues()
                                .calculateTopPadding() + 64.dp,
                            bottom = WindowInsets.navigationBars.asPaddingValues()
                                .calculateBottomPadding() + 200.dp,
                            start = 8.dp,
                            end = 8.dp
                        ),
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            PullRefreshIndicator(
                refreshing = collectionPhotosLazyItems.loadState.refresh is LoadState.Loading,
                state = pullRefreshState,
                modifier = Modifier.align(Alignment.TopCenter)
            )

            val colorizeTopBar by remember {
                derivedStateOf {
                    when (photosListLayoutType) {
                        PhotosListLayoutType.DEFAULT,
                        PhotosListLayoutType.MINIMAL_LIST -> {
                            listState.firstVisibleItemIndex > 0
                        }

                        PhotosListLayoutType.STAGGERED_GRID -> {
                            gridState.firstVisibleItemIndex > 0
                        }
                    }
                }
            }

            val topBarColor by animateColorAsState(
                targetValue = if (colorizeTopBar) {
                    MaterialTheme.colors.primary.copy(alpha = 0.9f)
                } else {
                    MaterialTheme.colors.primary.copy(alpha = 0.0f)
                },
                animationSpec = tween(durationMillis = 700, easing = LinearEasing),
                label = stringResource(id = R.string.collection_details_screen_top_bar_color_animation_label)
            )

            TopBar(
                titleText = collection.title,
                isOwnCollection = loggedInUsername.value == collection.username,
                onNavigateBack = navigateBack,
                onEditCollection = {
                    scope.launch {
                        modalBottomSheetState.show()
                    }
                },
                modifier = Modifier.drawBehind {
                    drawRect(topBarColor)
                }
            )
        }
    }
}