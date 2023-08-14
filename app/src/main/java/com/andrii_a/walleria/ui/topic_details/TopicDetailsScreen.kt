package com.andrii_a.walleria.ui.topic_details

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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.andrii_a.walleria.R
import com.andrii_a.walleria.domain.PhotoQuality
import com.andrii_a.walleria.domain.PhotosListLayoutType
import com.andrii_a.walleria.domain.models.photo.Photo
import com.andrii_a.walleria.domain.models.topic.Topic
import com.andrii_a.walleria.ui.common.PhotoId
import com.andrii_a.walleria.ui.common.TopicId
import com.andrii_a.walleria.ui.common.UserNickname
import com.andrii_a.walleria.ui.common.components.ErrorBanner
import com.andrii_a.walleria.ui.common.components.LoadingBanner
import com.andrii_a.walleria.ui.common.components.lists.PhotosGrid
import com.andrii_a.walleria.ui.common.components.lists.PhotosList
import com.andrii_a.walleria.ui.theme.PrimaryDark
import com.andrii_a.walleria.ui.theme.WalleriaTheme
import com.andrii_a.walleria.ui.util.openLinkInBrowser
import kotlinx.coroutines.launch

@Composable
fun TopicDetailsScreen(
    loadResult: TopicLoadResult,
    photosListLayoutType: PhotosListLayoutType,
    photosLoadQuality: PhotoQuality,
    onEvent: (TopicDetailsEvent) -> Unit,
    navigateBack: () -> Unit,
    navigateToPhotoDetails: (PhotoId) -> Unit,
    navigateToUserDetails: (UserNickname) -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        when (loadResult) {
            is TopicLoadResult.Empty -> Unit
            is TopicLoadResult.Loading -> {
                LoadingStateContent(
                    onNavigateBack = navigateBack,
                    modifier = Modifier
                        .fillMaxSize()
                        .statusBarsPadding()
                )
            }

            is TopicLoadResult.Error -> {
                ErrorStateContent(
                    onRetry = {
                        onEvent(TopicDetailsEvent.RequestTopic(TopicId(loadResult.topicId)))
                    },
                    onNavigateBack = navigateBack,
                    modifier = Modifier
                        .fillMaxSize()
                        .statusBarsPadding()
                )
            }

            is TopicLoadResult.Success -> {
                SuccessStateContent(
                    topic = loadResult.topic,
                    topicPhotosFilters = loadResult.currentFilters,
                    photosListLayoutType = photosListLayoutType,
                    photosLoadQuality = photosLoadQuality,
                    onEvent = onEvent,
                    topicPhotosLazyItems = loadResult.topicPhotos.collectAsLazyPagingItems(),
                    navigateToPhotoDetails = navigateToPhotoDetails,
                    navigateToUserDetails = navigateToUserDetails,
                    navigateBack = navigateBack
                )
            }
        }
    }
}

@Composable
private fun TopBar(
    modifier: Modifier = Modifier,
    titleText: String = "",
    onNavigateBack: () -> Unit,
    onFilterButtonClick: () -> Unit = {},
    onOpenTopicInBrowserButtonClick: () -> Unit = {}
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
        val (backButton, title, filterButton, openInBrowserButton) = createRefs()

        IconButton(
            onClick = onNavigateBack,
            modifier = Modifier
                .constrainAs(backButton) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start, 16.dp)
                    if (titleText.isNotBlank()) {
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

        if (titleText.isNotBlank()) {
            Text(
                text = titleText,
                style = MaterialTheme.typography.h6,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .constrainAs(title) {
                        top.linkTo(backButton.top)
                        bottom.linkTo(backButton.bottom)
                        start.linkTo(backButton.end, 16.dp)
                        end.linkTo(filterButton.start)
                        width = Dimension.fillToConstraints
                    }
                    .statusBarsPadding()
            )
        }

        IconButton(
            onClick = onFilterButtonClick,
            modifier = Modifier
                .constrainAs(filterButton) {
                    top.linkTo(backButton.top)
                    bottom.linkTo(backButton.bottom)
                    end.linkTo(openInBrowserButton.start)
                }
                .statusBarsPadding()
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_filter_outlined),
                contentDescription = stringResource(id = R.string.navigate_back)
            )
        }

        IconButton(
            onClick = onOpenTopicInBrowserButtonClick,
            modifier = Modifier
                .constrainAs(openInBrowserButton) {
                    top.linkTo(backButton.top)
                    bottom.linkTo(backButton.bottom)
                    end.linkTo(parent.end, 16.dp)
                }
                .statusBarsPadding()
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_web_outlined),
                contentDescription = stringResource(id = R.string.navigate_back)
            )
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
    topic: Topic,
    topicPhotosFilters: TopicPhotosFilters,
    photosListLayoutType: PhotosListLayoutType,
    photosLoadQuality: PhotoQuality,
    onEvent: (TopicDetailsEvent) -> Unit,
    topicPhotosLazyItems: LazyPagingItems<Photo>,
    navigateToPhotoDetails: (PhotoId) -> Unit,
    navigateToUserDetails: (UserNickname) -> Unit,
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val modalBottomSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true
    )

    ModalBottomSheetLayout(
        sheetContent = {
            TopicPhotosFilterBottomSheet(
                topicPhotosFilters = topicPhotosFilters,
                onApplyClick = onEvent,
                onDismiss = { scope.launch { modalBottomSheetState.hide() } }
            )
        },
        sheetState = modalBottomSheetState,
        sheetShape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        val pullRefreshState = rememberPullRefreshState(
            refreshing = topicPhotosLazyItems.loadState.refresh is LoadState.Loading,
            onRefresh = topicPhotosLazyItems::refresh,
            refreshingOffset = dimensionResource(id = R.dimen.top_bar_height) + WindowInsets.systemBars.asPaddingValues().calculateTopPadding()
        )

        Box(modifier = modifier.pullRefresh(pullRefreshState)) {
            val listState = rememberLazyListState()
            val gridState = rememberLazyStaggeredGridState()

            when (photosListLayoutType) {
                PhotosListLayoutType.DEFAULT -> {
                    PhotosList(
                        lazyPhotoItems = topicPhotosLazyItems,
                        headerContent = {
                            TopicDetailsDescriptionHeader(
                                topic = topic,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 24.dp, vertical = 16.dp)
                            )
                        },
                        onPhotoClicked = navigateToPhotoDetails,
                        onUserProfileClicked = navigateToUserDetails,
                        isCompact = false,
                        photosQuality = photosLoadQuality,
                        listState = listState,
                        contentPadding = PaddingValues(
                            top = WindowInsets.systemBars.asPaddingValues()
                                .calculateTopPadding() + dimensionResource(id = R.dimen.top_bar_height),
                            bottom = WindowInsets.navigationBars.asPaddingValues()
                                .calculateBottomPadding() + 200.dp
                        ),
                        modifier = Modifier.fillMaxSize(),
                    )
                }

                PhotosListLayoutType.MINIMAL_LIST -> {
                    PhotosList(
                        lazyPhotoItems = topicPhotosLazyItems,
                        headerContent = {
                            TopicDetailsDescriptionHeader(
                                topic = topic,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 24.dp, vertical = 16.dp)
                            )
                        },
                        onPhotoClicked = navigateToPhotoDetails,
                        onUserProfileClicked = navigateToUserDetails,
                        isCompact = true,
                        photosQuality = photosLoadQuality,
                        listState = listState,
                        contentPadding = PaddingValues(
                            top = WindowInsets.systemBars.asPaddingValues()
                                .calculateTopPadding() + dimensionResource(id = R.dimen.top_bar_height),
                            bottom = WindowInsets.navigationBars.asPaddingValues()
                                .calculateBottomPadding() + 200.dp
                        ),
                        modifier = Modifier.fillMaxSize(),
                    )
                }

                PhotosListLayoutType.STAGGERED_GRID -> {
                    PhotosGrid(
                        lazyPhotoItems = topicPhotosLazyItems,
                        headerContent = {
                            TopicDetailsDescriptionHeader(
                                topic = topic,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 24.dp, vertical = 16.dp)
                            )
                        },
                        onPhotoClicked = navigateToPhotoDetails,
                        photosQuality = photosLoadQuality,
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
                refreshing = topicPhotosLazyItems.loadState.refresh is LoadState.Loading,
                state = pullRefreshState,
                modifier = Modifier.align(Alignment.TopCenter)
            )

            val colorizeTopBar by remember {
                when (photosListLayoutType) {
                    PhotosListLayoutType.DEFAULT,
                    PhotosListLayoutType.MINIMAL_LIST -> {
                        derivedStateOf {
                            listState.firstVisibleItemIndex > 0
                        }
                    }
                    PhotosListLayoutType.STAGGERED_GRID -> {
                        derivedStateOf {
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
                titleText = topic.title,
                onNavigateBack = navigateBack,
                onFilterButtonClick = { scope.launch { modalBottomSheetState.show() } },
                onOpenTopicInBrowserButtonClick = { context.openLinkInBrowser(topic.links?.html) },
                modifier = Modifier.drawBehind {
                    drawRect(topBarColor)
                }
            )
        }
    }
}

@Preview
@Composable
fun TopBarPreview() {
    WalleriaTheme {
        TopBar(
            titleText = "Walleria",
            onNavigateBack = {},
            onOpenTopicInBrowserButtonClick = {}
        )
    }
}