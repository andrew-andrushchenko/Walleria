package com.andrii_a.walleria.ui.topic_details

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material.icons.outlined.OpenInBrowser
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
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
import com.andrii_a.walleria.ui.common.components.lists.PhotosGrid
import com.andrii_a.walleria.ui.common.components.lists.PhotosList
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
    when (loadResult) {
        is TopicLoadResult.Empty -> Unit
        is TopicLoadResult.Loading -> {
            LoadingStateContent(
                onNavigateBack = navigateBack
            )
        }

        is TopicLoadResult.Error -> {
            ErrorStateContent(
                onRetry = {
                    onEvent(TopicDetailsEvent.RequestTopic(TopicId(loadResult.topicId)))
                },
                onNavigateBack = navigateBack
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LoadingStateContent(onNavigateBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = stringResource(id = R.string.navigate_back),
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ErrorStateContent(
    onRetry: () -> Unit,
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = stringResource(id = R.string.navigate_back),
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        ErrorBanner(
            onRetry = onRetry,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
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
    navigateBack: () -> Unit
) {
    val context = LocalContext.current

    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    var openBottomSheet by rememberSaveable { mutableStateOf(false) }

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = topic.title,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = navigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = stringResource(id = R.string.navigate_back)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { openBottomSheet = !openBottomSheet }) {
                        Icon(
                            imageVector = Icons.Outlined.FilterList,
                            contentDescription = stringResource(id = R.string.filter)
                        )
                    }

                    IconButton(onClick = { context.openLinkInBrowser(topic.links?.html) }) {
                        Icon(
                            imageVector = Icons.Outlined.OpenInBrowser,
                            contentDescription = stringResource(id = R.string.open_in_browser)
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { innerPadding ->
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
                    photosLoadQuality = photosLoadQuality,
                    listState = listState,
                    contentPadding = innerPadding,
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
                    photosLoadQuality = photosLoadQuality,
                    listState = listState,
                    contentPadding = innerPadding,
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
                    photosLoadQuality = photosLoadQuality,
                    gridState = gridState,
                    contentPadding = innerPadding,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        val scope = rememberCoroutineScope()

        if (openBottomSheet) {
            val bottomPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

            ModalBottomSheet(
                onDismissRequest = { openBottomSheet = false },
                sheetState = bottomSheetState,
                windowInsets = WindowInsets(0)
            ) {
                TopicPhotosFilterBottomSheet(
                    topicPhotosFilters = topicPhotosFilters,
                    contentPadding = PaddingValues(
                        start = 16.dp,
                        end = 16.dp,
                        bottom = bottomPadding
                    ),
                    onApplyClick = onEvent,
                    onDismiss = {
                        scope.launch { bottomSheetState.hide() }.invokeOnCompletion {
                            if (!bottomSheetState.isVisible) {
                                openBottomSheet = false
                            }
                        }
                    }
                )
            }
        }
    }
}