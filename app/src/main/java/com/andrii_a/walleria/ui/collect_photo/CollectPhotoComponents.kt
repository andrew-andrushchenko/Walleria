package com.andrii_a.walleria.ui.collect_photo

import android.graphics.Bitmap
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddCircleOutline
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.RemoveCircleOutline
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.core.graphics.drawable.toDrawable
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.request.error
import coil3.request.fallback
import coil3.request.placeholder
import com.andrii_a.walleria.R
import com.andrii_a.walleria.domain.PhotoQuality
import com.andrii_a.walleria.domain.models.collection.Collection
import com.andrii_a.walleria.ui.collect_photo.state.CollectActionState
import com.andrii_a.walleria.ui.collect_photo.state.CollectionMetadata
import com.andrii_a.walleria.ui.common.components.CheckBoxRow
import com.andrii_a.walleria.ui.common.components.EmptyContentBanner
import com.andrii_a.walleria.ui.common.components.ErrorBanner
import com.andrii_a.walleria.ui.common.components.ErrorItem
import com.andrii_a.walleria.ui.common.components.LoadingListItem
import com.andrii_a.walleria.ui.theme.WalleriaTheme
import com.andrii_a.walleria.ui.util.BlurHashDecoder
import com.andrii_a.walleria.ui.util.abbreviatedNumberString
import com.andrii_a.walleria.ui.util.getUrlByQuality
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun UserCollectionsList(
    lazyCollectionItems: LazyPagingItems<Collection>,
    modifier: Modifier = Modifier,
    gridState: LazyStaggeredGridState = rememberLazyStaggeredGridState(),
    onCollectClick: (String) -> Unit,
    obtainCollectState: (String?) -> CollectActionState,
    modifiedCollectionMetadata: CollectionMetadata? = null,
    contentPadding: PaddingValues = PaddingValues()
) {
    Box(modifier = Modifier.fillMaxSize()) {
        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Adaptive(250.dp),
            state = gridState,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalItemSpacing = 8.dp,
            contentPadding = contentPadding,
            modifier = modifier.align(Alignment.TopCenter)
        ) {
            if (lazyCollectionItems.loadState.refresh is LoadState.NotLoading && lazyCollectionItems.itemCount > 0) {
                items(
                    count = lazyCollectionItems.itemCount,
                    key = lazyCollectionItems.itemKey { it.id }
                ) { index ->
                    val collection = lazyCollectionItems[index]
                    collection.let {
                        var collectState = obtainCollectState(collection?.id)

                        LaunchedEffect(key1 = modifiedCollectionMetadata) {
                            modifiedCollectionMetadata?.let { metadata ->
                                if (collection?.id == metadata.id) {
                                    collectState = metadata.state
                                }
                            }
                        }

                        UserCollectionItem(
                            collection = collection!!,
                            collectState = collectState,
                            onClick = { onCollectClick(collection.id) },
                            modifier = Modifier
                                .height(100.dp)
                                .animateItem()
                        )
                    }
                }
            }

            when (lazyCollectionItems.loadState.append) {
                is LoadState.NotLoading -> Unit

                is LoadState.Loading -> {
                    item(span = StaggeredGridItemSpan.FullLine) {
                        LoadingListItem(modifier = Modifier.fillMaxWidth())
                    }
                }

                is LoadState.Error -> {
                    item(span = StaggeredGridItemSpan.FullLine) {
                        ErrorItem(
                            onRetry = lazyCollectionItems::retry,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                        )
                    }
                }
            }
        }

        if (lazyCollectionItems.loadState.refresh is LoadState.Error) {
            ErrorBanner(
                onRetry = lazyCollectionItems::retry,
                modifier = Modifier.fillMaxSize()
            )
        }

        if (lazyCollectionItems.loadState.refresh is LoadState.Loading) {
            LoadingListItem(modifier = Modifier.fillMaxSize())
        }

        val shouldShowEmptyContent = lazyCollectionItems.loadState.refresh is LoadState.NotLoading
                && lazyCollectionItems.itemCount == 0

        if (shouldShowEmptyContent) {
            EmptyContentBanner(modifier = Modifier.fillMaxSize())
        }
    }
}

@Composable
fun UserCollectionItem(
    collection: Collection,
    collectState: CollectActionState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    ConstraintLayout(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .height(100.dp)
    ) {
        val (coverPhoto, dimmedOverlay, titleText,
            lockIcon, photosCountText, actionButton) = createRefs()

        val placeholderBitmap by produceState<Bitmap?>(initialValue = null) {
            value = withContext(Dispatchers.Default) {
                BlurHashDecoder.decode(
                    blurHash = collection.coverPhoto?.blurHash,
                    width = 4,
                    height = 3
                )
            }
        }

        val errorColor = MaterialTheme.colorScheme.surfaceColorAtElevation(8.dp)

        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(collection.coverPhoto?.getUrlByQuality(quality = PhotoQuality.MEDIUM))
                .crossfade(durationMillis = 1000)
                .placeholder(placeholderBitmap?.toDrawable(context.resources))
                .fallback(placeholderBitmap?.toDrawable(context.resources))
                .error(errorColor.toArgb().toDrawable())
                .build(),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
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

        if (collection.isPrivate) {
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
            text = collection.title,
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
                collection.totalPhotos.abbreviatedNumberString
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
                CollectActionState.Loading -> {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }

                CollectActionState.Collected -> {
                    IconButton(onClick = onClick) {
                        Icon(
                            imageVector = Icons.Outlined.RemoveCircleOutline,
                            tint = Color.White,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                CollectActionState.NotCollected -> {
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
fun CreateAndCollectBottomSheet(
    contentPadding: PaddingValues = PaddingValues(),
    onConfirm: (String, String, Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    var title by rememberSaveable { mutableStateOf("") }
    var description by rememberSaveable { mutableStateOf("") }
    var isPrivate by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(contentPadding)
            .verticalScroll(rememberScrollState())
    ) {
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

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(onClick = { onConfirm(title, description, isPrivate) }) {
                Text(text = stringResource(id = R.string.action_done))
            }

            Spacer(modifier = Modifier.width(8.dp))

            OutlinedButton(onClick = onDismiss) {
                Text(text = stringResource(id = R.string.action_cancel))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateCollectionProgressDialog() {
    BasicAlertDialog(
        onDismissRequest = {},
        content = {
            LoadingListItem(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
        }
    )
}

@Preview
@Composable
private fun CreateAndCollectBottomSheetPreview() {
    WalleriaTheme {
        Surface {
            CreateAndCollectBottomSheet(
                contentPadding = PaddingValues(8.dp),
                onConfirm = { _, _, _ -> },
                onDismiss = {}
            )
        }
    }
}
