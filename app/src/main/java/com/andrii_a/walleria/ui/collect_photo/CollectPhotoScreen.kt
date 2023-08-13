package com.andrii_a.walleria.ui.collect_photo

import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import com.andrii_a.walleria.domain.models.collection.Collection
import com.andrii_a.walleria.ui.common.PhotoId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

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
    ) -> SharedFlow<CollectionCreationResult>
) {
    val coroutineScope = rememberCoroutineScope()

    Surface(
        color = MaterialTheme.colors.primary,
        shape = RoundedCornerShape(16.dp)
    ) {
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
            photoId = photoId
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

