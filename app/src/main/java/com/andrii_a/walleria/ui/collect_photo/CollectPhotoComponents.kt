package com.andrii_a.walleria.ui.collect_photo

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.graphics.drawable.ColorDrawable
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddCircleOutline
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.RemoveCircleOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.andrii_a.walleria.R
import com.andrii_a.walleria.domain.PhotoQuality
import com.andrii_a.walleria.domain.models.collection.Collection
import com.andrii_a.walleria.ui.common.PhotoId
import com.andrii_a.walleria.ui.common.components.CheckBoxRow
import com.andrii_a.walleria.ui.theme.WalleriaTheme
import com.andrii_a.walleria.ui.util.abbreviatedNumberString
import com.andrii_a.walleria.ui.util.getUrlByQuality
import com.andrii_a.walleria.ui.util.primaryColorComposable
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch

@Composable
fun UserCollectionsList(
    lazyCollectionItems: LazyPagingItems<Collection>,
    listState: LazyListState,
    onCreateNewCollection: () -> Unit,
    isCollectionInList: (collectionId: String) -> Boolean,
    collectPhoto: (collectionId: String, photoId: String) -> SharedFlow<CollectState>,
    dropPhoto: (collectionId: String, photoId: String) -> SharedFlow<CollectState>,
    photoId: PhotoId,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues()
) {
    val scope = rememberCoroutineScope()

    LazyColumn(
        state = listState,
        contentPadding = contentPadding,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
    ) {
        item {
            CreateNewCollectionButton(
                onClick = onCreateNewCollection,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .padding(horizontal = 16.dp)
            )
        }

        items(
            count = lazyCollectionItems.itemCount,
            key = lazyCollectionItems.itemKey { it.id }
        ) { index ->
            val collection = lazyCollectionItems[index]
            collection?.let {
                var collectState by remember {
                    mutableStateOf(
                        if (isCollectionInList(collection.id))
                            CollectState.Collected(collection.coverPhoto)
                        else
                            CollectState.NotCollected(collection.coverPhoto)
                    )
                }

                val onClick: () -> Unit = {
                    scope.launch {
                        when (collectState) {
                            is CollectState.Collected -> {
                                dropPhoto(
                                    collection.id,
                                    photoId.value
                                ).collect {
                                    collectState = it
                                }
                            }

                            is CollectState.NotCollected -> {
                                collectPhoto(
                                    collection.id,
                                    photoId.value
                                ).collect {
                                    collectState = it
                                }
                            }

                            else -> Unit
                        }
                    }
                }

                UserCollectionItem(
                    title = collection.title,
                    totalPhotos = collection.totalPhotos,
                    isPrivate = collection.isPrivate,
                    collectState = collectState,
                    onClick = onClick,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }
    }
}

@Composable
fun UserCollectionItem(
    title: String,
    totalPhotos: Long,
    isPrivate: Boolean,
    collectState: CollectState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ConstraintLayout(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .fillMaxWidth()
            .height(100.dp)

    ) {
        val (coverPhoto, dimmedOverlay, titleText,
            lockIcon, photosCountText, actionButton) = createRefs()

        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(collectState.newCoverPhoto?.getUrlByQuality(quality = PhotoQuality.MEDIUM))
                .crossfade(durationMillis = 1000)
                .placeholder(
                    ColorDrawable(
                        collectState.newCoverPhoto?.primaryColorComposable?.toArgb()
                            ?: Color.Gray.toArgb()
                    )
                )
                .error(
                    ColorDrawable(
                        collectState.newCoverPhoto?.primaryColorComposable?.toArgb()
                            ?: Color.Gray.toArgb()
                    )
                )
                .build(),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .constrainAs(coverPhoto) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        )

        Spacer(
            modifier = Modifier
                .constrainAs(dimmedOverlay) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    width = Dimension.fillToConstraints
                    height = Dimension.fillToConstraints
                }
                .background(color = Color.Black.copy(alpha = 0.5f))
        )

        if (isPrivate) {
            Icon(
                imageVector = Icons.Outlined.Lock,
                tint = Color.White,
                contentDescription = null,
                modifier = Modifier
                    .constrainAs(lockIcon) {
                        start.linkTo(parent.start, margin = 8.dp)
                        bottom.linkTo(titleText.top, margin = 8.dp)
                    }
                    .size(18.dp)
            )
        }

        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = Color.White,
            modifier = Modifier.constrainAs(titleText) {
                start.linkTo(parent.start, margin = 8.dp)
                bottom.linkTo(photosCountText.top, margin = 8.dp)
                end.linkTo(actionButton.start, margin = 8.dp)
                width = Dimension.fillToConstraints
            }
        )

        Text(
            text = stringResource(
                id = R.string.photos_title_template,
                totalPhotos.abbreviatedNumberString
            ),
            style = MaterialTheme.typography.bodySmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = Color.White,
            modifier = Modifier.constrainAs(photosCountText) {
                start.linkTo(parent.start, margin = 8.dp)
                bottom.linkTo(parent.bottom, margin = 8.dp)
            }
        )

        AnimatedContent(
            targetState = collectState,
            label = stringResource(id = R.string.collect_button_animated_content_label),
            modifier = Modifier.constrainAs(actionButton) {
                start.linkTo(titleText.end)
                end.linkTo(parent.end, margin = 8.dp)
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
            }
        ) { state ->
            when (state) {
                is CollectState.Loading -> {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }

                is CollectState.Collected -> {
                    IconButton(onClick = onClick) {
                        Icon(
                            imageVector = Icons.Outlined.RemoveCircleOutline,
                            tint = Color.White,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                is CollectState.NotCollected -> {
                    IconButton(onClick = onClick) {
                        Icon(
                            imageVector = Icons.Outlined.AddCircleOutline,
                            tint = Color.White,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CreateNewCollectionButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val rectColor = MaterialTheme.colorScheme.onSurface
    val stroke = Stroke(
        width = 3f,
        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .drawBehind {
                drawRoundRect(
                    color = rectColor,
                    style = stroke,
                    cornerRadius = CornerRadius(8.dp.toPx())
                )
            }
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
    ) {
        Text(text = stringResource(id = R.string.create_new_and_add))
    }
}

@Composable
fun CreateCollectionAndCollectDialog(
    photoId: String,
    createAndCollect: (
        title: String,
        description: String?,
        isPrivate: Boolean,
        photoId: String
    ) -> SharedFlow<CollectionCreationResult>,
    onDismiss: () -> Unit
) {
    val scope = rememberCoroutineScope()

    var title by rememberSaveable { mutableStateOf("") }
    var description by rememberSaveable { mutableStateOf("") }
    var isPrivate by rememberSaveable { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                Icons.Outlined.AddCircleOutline,
                contentDescription = null,
            )
        },
        title = { Text(text = stringResource(id = R.string.create_new_and_add)) },
        text = {
            Column {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text(text = stringResource(id = R.string.collection_name_hint)) },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text(text = stringResource(id = R.string.collection_description_hint)) },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                CheckBoxRow(
                    checked = isPrivate,
                    onCheckedChange = { isPrivate = it },
                    labelText = stringResource(id = R.string.collection_private),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    scope.launch {
                        createAndCollect(
                            title,
                            description,
                            isPrivate,
                            photoId
                        ).collect { result ->
                            when (result) {
                                is CollectionCreationResult.Success -> onDismiss()
                                is CollectionCreationResult.Loading -> Unit
                                is CollectionCreationResult.Error -> Unit
                            }
                        }
                    }
                }
            ) {
                Text(text = stringResource(id = R.string.action_done))
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text(text = stringResource(id = R.string.action_cancel))
            }
        }
    )
}

@Preview
@Composable
fun UserCollectionItemPreview() {
    WalleriaTheme {
        UserCollectionItem(
            title = "My collection has extremely huge title and it does not fit anymore",
            totalPhotos = 100000,
            isPrivate = true,
            collectState = CollectState.NotCollected(null),
            onClick = {},
        )
    }
}

@Preview
@Composable
fun CreateNewCollectionButtonPrev() {
    WalleriaTheme {
        CreateNewCollectionButton(
            onClick = {},
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
        )
    }
}

@Preview(uiMode = UI_MODE_NIGHT_NO)
@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
fun CreateCollectionAndCollectDialogPreview() {
    WalleriaTheme {
        val scope = rememberCoroutineScope()
        CreateCollectionAndCollectDialog(
            photoId = "",
            createAndCollect = { _, _, _, _ ->
                emptyFlow<CollectionCreationResult>().shareIn(
                    scope = scope,
                    started = SharingStarted.WhileSubscribed()
                )
            },
            onDismiss = {}
        )
    }
}
