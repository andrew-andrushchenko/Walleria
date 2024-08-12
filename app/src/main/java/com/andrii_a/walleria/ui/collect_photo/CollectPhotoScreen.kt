package com.andrii_a.walleria.ui.collect_photo

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.andrii_a.walleria.R
import com.andrii_a.walleria.domain.models.collection.Collection
import com.andrii_a.walleria.ui.collect_photo.event.CollectPhotoEvent
import com.andrii_a.walleria.ui.collect_photo.state.CollectActionState
import com.andrii_a.walleria.ui.collect_photo.state.CollectPhotoUiState
import com.andrii_a.walleria.ui.common.components.ErrorBanner
import com.andrii_a.walleria.ui.util.toast
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

@Composable
fun CollectPhotoScreen(
    state: CollectPhotoUiState,
    onEvent: (CollectPhotoEvent) -> Unit
) {
    val userCollections = remember(state.userCollectionsPagingData) {
        flow {
            emit(state.userCollectionsPagingData)
        }
    }.collectAsLazyPagingItems()

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
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.select_collections)) },
                navigationIcon = {
                    IconButton(onClick = { onEvent(CollectPhotoEvent.GoBack) }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = stringResource(id = R.string.navigate_back),
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
            )
        },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { innerPadding ->
        val lazyListState = rememberLazyListState()

        UserCollectionsList(
            userCollections = userCollections,
            onCreateNewClick = { onEvent(CollectPhotoEvent.OpenCreateAndCollectDialog) },
            onCollectClick = { collectionId ->
                val collectState = if (state.userCollectionsContainingPhoto.contains(collectionId)) {
                    CollectActionState.Collected
                } else {
                    CollectActionState.NotCollected
                }

                val event =
                    if (collectState == CollectActionState.Collected) {
                        CollectPhotoEvent.DropPhotoFromCollection(
                            collectionId = collectionId,
                            photoId = state.photoId.value
                        )
                    } else {
                        CollectPhotoEvent.CollectPhoto(
                            collectionId = collectionId,
                            photoId = state.photoId.value
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
            listState = lazyListState,
            contentPadding = innerPadding,
            modifier = Modifier.navigationBarsPadding()
        )

        val scope = rememberCoroutineScope()

        val bottomSheetState = rememberModalBottomSheetState(
            skipPartiallyExpanded = true
        )

        if (state.isCreateDialogOpened) {
            val bottomPadding =
                WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

            ModalBottomSheet(
                onDismissRequest = { onEvent(CollectPhotoEvent.DismissCreateAndCollectDialog) },
                sheetState = bottomSheetState,
                windowInsets = WindowInsets(0)
            ) {
                CreateAndCollectBottomSheet(
                    contentPadding = PaddingValues(
                        start = 16.dp,
                        end = 16.dp,
                        bottom = bottomPadding
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


