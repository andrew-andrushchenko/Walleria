package com.andrii_a.walleria.ui.collect_photo

import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import com.andrii_a.walleria.R
import com.andrii_a.walleria.domain.models.collection.Collection
import com.andrii_a.walleria.ui.common.PhotoId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollectPhotoScreen(
    photoId: PhotoId,
    userCollections: Flow<PagingData<Collection>>,
    isCollectionInList: (collectionId: String) -> Boolean,
    collectPhoto: (collectionId: String, photoId: String) -> SharedFlow<CollectState>,
    dropPhoto: (collectionId: String, photoId: String) -> SharedFlow<CollectState>,
    createAndCollect: (
        title: String,
        description: String?,
        isPrivate: Boolean,
        photoId: String
    ) -> SharedFlow<CollectionCreationResult>,
    onNavigateBack: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.select_collections)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
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
        val lazyPagingItems = userCollections.collectAsLazyPagingItems()

        var showCreateCollectionDialog by rememberSaveable {
            mutableStateOf(false)
        }

        val lazyListState = rememberLazyListState()

        UserCollectionsList(
            lazyCollectionItems = lazyPagingItems,
            listState = lazyListState,
            onCreateNewCollection = { showCreateCollectionDialog = true },
            isCollectionInList = isCollectionInList,
            collectPhoto = collectPhoto,
            dropPhoto = dropPhoto,
            photoId = photoId,
            contentPadding = innerPadding,
            modifier = Modifier.navigationBarsPadding()
        )

        if (showCreateCollectionDialog) {
            CreateCollectionAndCollectDialog(
                photoId = photoId.value,
                createAndCollect = createAndCollect,
                onDismiss = {
                    lazyPagingItems.refresh()
                    coroutineScope.launch {
                        lazyListState.animateScrollToItem(0)
                    }
                    showCreateCollectionDialog = false
                }
            )
        }
    }

}

