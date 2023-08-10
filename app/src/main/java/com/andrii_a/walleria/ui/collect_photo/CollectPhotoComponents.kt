package com.andrii_a.walleria.ui.collect_photo

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.graphics.drawable.ColorDrawable
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.andrii_a.walleria.R
import com.andrii_a.walleria.core.PhotoQuality
import com.andrii_a.walleria.domain.models.collection.Collection
import com.andrii_a.walleria.ui.common.components.CheckBoxRow
import com.andrii_a.walleria.ui.common.PhotoId
import com.andrii_a.walleria.ui.common.components.WButton
import com.andrii_a.walleria.ui.common.components.WOutlinedTextField
import com.andrii_a.walleria.ui.theme.PrimaryLight
import com.andrii_a.walleria.ui.theme.WalleriaTheme
import com.andrii_a.walleria.ui.util.abbreviatedNumberString
import com.andrii_a.walleria.ui.util.getUrlByQuality
import com.andrii_a.walleria.ui.util.primaryColorComposable
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun UserCollectionsList(
    lazyCollectionItems: LazyPagingItems<Collection>,
    listState: LazyListState,
    onCreateNewCollection: () -> Unit,
    isCollectionInList: (collectionId: String) -> Boolean,
    collectPhoto: (collectionId: String, photoId: String) -> SharedFlow<CollectState>,
    dropPhoto: (collectionId: String, photoId: String) -> SharedFlow<CollectState>,
    photoId: PhotoId,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()

    LazyColumn(
        state = listState,
        contentPadding = PaddingValues(
            bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
        ),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier.defaultMinSize(minHeight = 500.dp)
    ) {
        stickyHeader {
            Text(
                text = stringResource(id = R.string.select_collections),
                style = MaterialTheme.typography.h5,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = MaterialTheme.colors.primary)
                    .padding(top = 16.dp, start = 16.dp, end = 16.dp)
            )
        }

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
                painter = painterResource(id = R.drawable.ic_lock_outlined),
                tint = PrimaryLight,
                contentDescription = null,
                modifier = Modifier
                    .constrainAs(lockIcon) {
                        start.linkTo(parent.start, margin = 8.dp)
                        bottom.linkTo(titleText.top, margin = 4.dp)
                    }
                    .size(18.dp)
            )
        }

        Text(
            text = title,
            style = MaterialTheme.typography.subtitle1,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = PrimaryLight,
            modifier = Modifier.constrainAs(titleText) {
                start.linkTo(parent.start, margin = 8.dp)
                bottom.linkTo(photosCountText.top, margin = 4.dp)
                end.linkTo(actionButton.start, margin = 0.dp)
                width = Dimension.fillToConstraints
            }
        )

        Text(
            text = stringResource(
                id = R.string.photos_title_template,
                totalPhotos.abbreviatedNumberString
            ),
            style = MaterialTheme.typography.subtitle2,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = PrimaryLight,
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
                        color = PrimaryLight,
                        modifier = Modifier.size(24.dp)
                    )
                }

                is CollectState.Collected -> {
                    IconButton(onClick = onClick) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_remove_outlined),
                            tint = PrimaryLight,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                is CollectState.NotCollected -> {
                    IconButton(onClick = onClick) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_add_outlined),
                            tint = PrimaryLight,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun UserCollectionItemPreview() {
    WalleriaTheme {
        UserCollectionItem(
            title = "My collection has extremely huge title and it does not fit anymore",
            totalPhotos = 100000,
            isPrivate = true,
            collectState = CollectState.Loading,
            onClick = {},
        )
    }
}

@Composable
fun CreateNewCollectionButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val rectColor = MaterialTheme.colors.onPrimary
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

@Preview(uiMode = UI_MODE_NIGHT_NO)
@Preview(uiMode = UI_MODE_NIGHT_YES)
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

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colors.background
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = stringResource(id = R.string.create_new_and_add),
                    style = MaterialTheme.typography.h5,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                WOutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text(text = stringResource(id = R.string.collection_name_hint)) },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                WOutlinedTextField(
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

                Spacer(modifier = Modifier.height(8.dp))

                WButton(
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
                    },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text(
                        text = stringResource(id = R.string.done_collection_creation),
                        style = MaterialTheme.typography.subtitle1
                    )
                }
            }
        }
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
