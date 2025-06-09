package com.andrii_a.walleria.ui.collect_photo

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.CreateNewFolder
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.andrii_a.walleria.R
import com.andrii_a.walleria.domain.models.collection.Collection
import com.andrii_a.walleria.ui.collect_photo.event.CollectPhotoEvent
import com.andrii_a.walleria.ui.collect_photo.state.CollectActionState
import com.andrii_a.walleria.ui.collect_photo.state.CollectPhotoUiState
import com.andrii_a.walleria.ui.common.components.ErrorBanner
import com.andrii_a.walleria.ui.common.components.WLoadingIndicator
import com.andrii_a.walleria.ui.theme.WalleriaTheme
import com.andrii_a.walleria.ui.util.toast
import kotlinx.coroutines.launch

@Composable
fun CollectPhotoScreen(
    state: CollectPhotoUiState,
    onEvent: (CollectPhotoEvent) -> Unit
) {
    val userCollections by rememberUpdatedState(newValue = state.userCollections.collectAsLazyPagingItems())

    when {
        state.isLoading -> {
            LoadingStateContent(onNavigateBack = { onEvent(CollectPhotoEvent.GoBack) })
        }

        !state.isLoading && state.error == null -> {
            SuccessStateContent(
                state = state,
                userCollections = userCollections,
                onEvent = onEvent
            )
        }

        else -> {
            when (val error = state.error) {
                is ListLoadingError -> {
                    ErrorBanner(
                        onRetry = userCollections::retry,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                is CollectOperationError -> {
                    val context = LocalContext.current
                    context.toast(error.reason.asString())
                }

                else -> Unit
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SuccessStateContent(
    state: CollectPhotoUiState,
    userCollections: LazyPagingItems<Collection>,
    onEvent: (CollectPhotoEvent) -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = stringResource(id = R.string.select_collections)) },
                navigationIcon = {
                    IconButton(onClick = { onEvent(CollectPhotoEvent.GoBack) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = stringResource(id = R.string.navigate_back),
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { onEvent(CollectPhotoEvent.OpenCreateAndCollectDialog) }) {
                        Icon(
                            imageVector = Icons.Outlined.CreateNewFolder,
                            contentDescription = stringResource(id = R.string.create_new_and_add),
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
            )
        },
        contentWindowInsets = WindowInsets.safeDrawing,
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { innerPadding ->
        UserCollectionsList(
            lazyCollectionItems = userCollections,
            onCollectClick = { collectionId ->
                val collectState =
                    if (state.userCollectionsContainingPhoto.contains(collectionId)) {
                        CollectActionState.Collected
                    } else {
                        CollectActionState.NotCollected
                    }

                val event =
                    if (collectState == CollectActionState.Collected) {
                        CollectPhotoEvent.DropPhotoFromCollection(
                            collectionId = collectionId,
                            photoId = state.photoId
                        )
                    } else {
                        CollectPhotoEvent.CollectPhoto(
                            collectionId = collectionId,
                            photoId = state.photoId
                        )
                    }

                onEvent(event)
            },
            obtainCollectState = { collectionId ->
                if (state.userCollectionsContainingPhoto.contains(collectionId)) {
                    CollectActionState.Collected
                } else {
                    CollectActionState.NotCollected
                }
            },
            modifiedCollectionMetadata = state.modifiedCollectionMetadata,
            contentPadding = PaddingValues(
                top = 16.dp,
                start = 16.dp,
                end = 16.dp,
                bottom = WindowInsets.systemBars.asPaddingValues().calculateBottomPadding(),
            ),
            modifier = Modifier
                .padding(innerPadding)
                .navigationBarsPadding()
        )

        val scope = rememberCoroutineScope()

        val bottomSheetState = rememberModalBottomSheetState(
            skipPartiallyExpanded = true
        )

        if (state.isCreateDialogOpened) {
            ModalBottomSheet(
                onDismissRequest = { onEvent(CollectPhotoEvent.DismissCreateAndCollectDialog) },
                sheetState = bottomSheetState
            ) {
                CreateAndCollectBottomSheet(
                    contentPadding = PaddingValues(
                        start = 16.dp,
                        end = 16.dp
                    ),
                    onConfirm = { title, description, isPrivate ->
                        onEvent(
                            CollectPhotoEvent.CreateCollectionAndCollect(
                                title, description, isPrivate, state.photoId
                            )
                        )
                        onEvent(CollectPhotoEvent.DismissCreateAndCollectDialog)
                    },
                    onDismiss = {
                        scope.launch { bottomSheetState.hide() }.invokeOnCompletion {
                            if (!bottomSheetState.isVisible) {
                                onEvent(CollectPhotoEvent.DismissCreateAndCollectDialog)
                            }
                        }
                    }
                )
            }
        }

        if (state.isCreateCollectionInProgress) {
            CreateCollectionProgressDialog()
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
            WLoadingIndicator()
        }
    }
}

@PreviewScreenSizes
@Composable
private fun CollectPhotoScreenPreview() {
    WalleriaTheme {
        val collections = (0..3).map { number ->
            Collection(
                id = number.toString(),
                title = "Collection $number",
                description = null,
                curated = false,
                featured = false,
                totalPhotos = number.toLong(),
                isPrivate = false,
                tags = null,
                coverPhoto = null,
                previewPhotos = null,
                links = null,
                user = null
            )
        }

        val state = CollectPhotoUiState(
            userCollectionsPagingData = PagingData.from(collections)
        )

        CollectPhotoScreen(
            state = state,
            onEvent = {}
        )
    }
}


