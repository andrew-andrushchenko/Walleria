package com.andrii_a.walleria.ui.topic_details

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.paging.compose.collectAsLazyPagingItems
import com.andrii_a.walleria.R
import com.andrii_a.walleria.domain.TopicStatus
import com.andrii_a.walleria.domain.models.topic.Topic
import com.andrii_a.walleria.domain.models.user.User
import com.andrii_a.walleria.ui.common.UiErrorWithRetry
import com.andrii_a.walleria.ui.common.components.ErrorBanner
import com.andrii_a.walleria.ui.common.components.PhotosGridContent
import com.andrii_a.walleria.ui.common.components.WLoadingIndicator
import com.andrii_a.walleria.ui.theme.WalleriaTheme
import kotlinx.coroutines.launch

@Composable
fun TopicDetailsScreen(
    state: TopicDetailsUiState,
    onEvent: (TopicDetailsEvent) -> Unit
) {
    when {
        state.isLoading -> {
            LoadingStateContent(
                onNavigateBack = { onEvent(TopicDetailsEvent.GoBack) }
            )
        }

        !state.isLoading && state.error == null && state.topic != null -> {
            SuccessStateContent(
                state = state,
                onEvent = onEvent,
            )
        }

        else -> {
            ErrorStateContent(
                onRetry = {
                    val error = state.error as? UiErrorWithRetry
                    error?.onRetry?.invoke()
                },
                onNavigateBack = { onEvent(TopicDetailsEvent.GoBack) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LoadingStateContent(onNavigateBack: () -> Unit) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {},
                navigationIcon = {
                    FilledTonalIconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = stringResource(id = R.string.navigate_back),
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            WLoadingIndicator()
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
            CenterAlignedTopAppBar(
                title = {},
                navigationIcon = {
                    FilledTonalIconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
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
    state: TopicDetailsUiState,
    onEvent: (TopicDetailsEvent) -> Unit,
) {
    val topic = state.topic!!

    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = topic.title,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                },
                navigationIcon = {
                    FilledTonalIconButton(
                        onClick = { onEvent(TopicDetailsEvent.GoBack) },
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = stringResource(id = R.string.navigate_back)
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onEvent(TopicDetailsEvent.OpenFilterDialog) },
            ) {
                Icon(
                    imageVector = Icons.Outlined.FilterList,
                    contentDescription = stringResource(id = R.string.filter)
                )
            }
        },
        contentWindowInsets = WindowInsets.safeDrawing,
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { innerPadding ->
        val topicPhotosLazyItems by rememberUpdatedState(newValue = state.topicPhotos.collectAsLazyPagingItems())

        PhotosGridContent(
            photoItems = topicPhotosLazyItems,
            onPhotoClicked = { onEvent(TopicDetailsEvent.SelectPhoto(it)) },
            headerContent = {
                TopicDetailsDescriptionHeader(
                    topic = topic,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                )
            },
            contentPadding = PaddingValues(
                top = innerPadding.calculateTopPadding(),
                bottom = innerPadding.calculateBottomPadding() + 150.dp,
                start = innerPadding.calculateStartPadding(LayoutDirection.Ltr),
                end = innerPadding.calculateEndPadding(LayoutDirection.Ltr)
            ),
            scrollToTopButtonPadding = PaddingValues(
                bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
            )
        )

        val scope = rememberCoroutineScope()

        if (state.isFilterDialogOpened) {
            ModalBottomSheet(
                onDismissRequest = { onEvent(TopicDetailsEvent.DismissFilterDialog) },
                sheetState = bottomSheetState
            ) {
                TopicPhotosFilterBottomSheet(
                    topicPhotosFilters = state.topicPhotosFilters,
                    contentPadding = PaddingValues(
                        start = 16.dp,
                        end = 16.dp
                    ),
                    onApplyClick = onEvent,
                    onDismiss = {
                        scope.launch { bottomSheetState.hide() }.invokeOnCompletion {
                            if (!bottomSheetState.isVisible) {
                                onEvent(TopicDetailsEvent.DismissFilterDialog)
                            }
                        }
                    }
                )
            }
        }
    }
}

@Preview
@Composable
private fun TopicDetailsScreenPreview() {
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

        val topic = Topic(
            id = "",
            title = "Wallpapers",
            description = "Lorem ipsum dolor sit amet".repeat(10),
            featured = false,
            startsAt = "",
            endsAt = "",
            updatedAt = "",
            totalPhotos = 856_000,
            links = null,
            status = TopicStatus.OPEN,
            owners = listOf(user),
            coverPhoto = null,
            previewPhotos = null
        )

        val state = TopicDetailsUiState(topic = topic)

        TopicDetailsScreen(
            state = state,
            onEvent = {}
        )
    }
}