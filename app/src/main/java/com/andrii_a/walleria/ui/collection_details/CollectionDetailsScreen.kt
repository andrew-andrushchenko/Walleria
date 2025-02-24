package com.andrii_a.walleria.ui.collection_details

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.paging.compose.collectAsLazyPagingItems
import com.andrii_a.walleria.R
import com.andrii_a.walleria.ui.common.UiErrorWithRetry
import com.andrii_a.walleria.ui.common.components.ErrorBanner
import com.andrii_a.walleria.ui.common.components.LoadingListItem
import com.andrii_a.walleria.ui.common.components.PhotosGridContent
import com.andrii_a.walleria.ui.util.username
import kotlinx.coroutines.launch

@Composable
fun CollectionDetailsScreen(
    state: CollectionDetailsUiState,
    onEvent: (CollectionDetailsEvent) -> Unit,
) {
    when {
        state.isLoading -> {
            LoadingStateContent(
                onNavigateBack = { onEvent(CollectionDetailsEvent.GoBack) }
            )
        }

        !state.isLoading && state.error == null && state.collection != null -> {
            SuccessStateContent(
                state = state,
                onEvent = onEvent
            )
        }

        else -> {
            ErrorStateContent(
                onRetry = {
                    val error = state.error as? UiErrorWithRetry
                    error?.onRetry?.invoke()
                },
                onNavigateBack = { onEvent(CollectionDetailsEvent.GoBack) }
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
                    IconButton(onClick = onNavigateBack) {
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
            LoadingListItem()
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
                    IconButton(onClick = onNavigateBack) {
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
    state: CollectionDetailsUiState,
    onEvent: (CollectionDetailsEvent) -> Unit,
) {
    val collection = state.collection!!

    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    var openBottomSheet by rememberSaveable { mutableStateOf(false) }

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = collection.title,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { onEvent(CollectionDetailsEvent.GoBack) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = stringResource(id = R.string.navigate_back)
                        )
                    }
                },
                actions = {
                    if (state.loggedInUserNickname == collection.username) {
                        IconButton(onClick = { openBottomSheet = !openBottomSheet }) {
                            Icon(
                                imageVector = Icons.Outlined.Edit,
                                contentDescription = stringResource(id = R.string.edit_collection)
                            )
                        }
                    }
                },
                scrollBehavior = scrollBehavior
            )
        },
        contentWindowInsets = WindowInsets.safeDrawing,
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { innerPadding ->
        val collectionPhotosLazyItems by rememberUpdatedState(newValue = state.collectionPhotos.collectAsLazyPagingItems())

        PhotosGridContent(
            photoItems = collectionPhotosLazyItems,
            onPhotoClicked = { onEvent(CollectionDetailsEvent.SelectPhoto(it)) },
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
            contentPadding = PaddingValues(
                top = 16.dp,
                start = 16.dp,
                end = 16.dp,
                bottom = WindowInsets.systemBars.asPaddingValues().calculateBottomPadding(),
            ),
            scrollToTopButtonPadding = PaddingValues(
                bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
            ),
            modifier = Modifier.padding(innerPadding)
        )

        val scope = rememberCoroutineScope()

        if (openBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = { openBottomSheet = false },
                sheetState = bottomSheetState
            ) {
                EditCollectionInfoBottomSheet(
                    collection = collection,
                    contentPadding = PaddingValues(
                        start = 16.dp,
                        end = 16.dp
                    ),
                    onEvent = onEvent,
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