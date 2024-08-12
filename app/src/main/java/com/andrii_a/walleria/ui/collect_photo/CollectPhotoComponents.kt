package com.andrii_a.walleria.ui.collect_photo

import android.graphics.drawable.ColorDrawable
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddCircleOutline
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.RemoveCircleOutline
import androidx.compose.material3.AlertDialog
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
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
import com.andrii_a.walleria.ui.collect_photo.state.CollectActionState
import com.andrii_a.walleria.ui.collect_photo.state.CollectionMetadata
import com.andrii_a.walleria.ui.common.components.CheckBoxRow
import com.andrii_a.walleria.ui.common.components.LoadingListItem
import com.andrii_a.walleria.ui.theme.WalleriaTheme
import com.andrii_a.walleria.ui.util.abbreviatedNumberString
import com.andrii_a.walleria.ui.util.getUrlByQuality
import com.andrii_a.walleria.ui.util.primaryColorInt

@Composable
fun UserCollectionsList(
    userCollections: LazyPagingItems<Collection>,
    listState: LazyListState,
    onCreateNewClick: () -> Unit,
    onCollectClick: (String) -> Unit,
    obtainCollectState: (String?) -> CollectActionState,
    modifier: Modifier = Modifier,
    modifiedCollectionMetadata: CollectionMetadata? = null,
    contentPadding: PaddingValues = PaddingValues()
) {
    LazyColumn(
        state = listState,
        contentPadding = contentPadding,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
    ) {
        item {
            CreateNewCollectionButton(
                onClick = onCreateNewClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .padding(horizontal = 16.dp)
            )
        }

        items(
            count = userCollections.itemCount,
            key = userCollections.itemKey { it.id }
        ) { index ->
            val collection = userCollections[index]
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
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
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
            .fillMaxWidth()
            .height(100.dp)

    ) {
        val (coverPhoto, dimmedOverlay, titleText,
            lockIcon, photosCountText, actionButton) = createRefs()

        /*val placeholderBitmap by produceState<Bitmap?>(initialValue = null) {
            value = withContext(Dispatchers.Default) {
                BlurHashDecoder.decode(
                    blurHash = collectState.newCoverPhoto?.blurHash,
                    width = 4,
                    height = 3
                )
            }
        }*/

        AsyncImage(
            model = ImageRequest.Builder(context)
                //.data(collectState.newCoverPhoto?.getUrlByQuality(quality = PhotoQuality.MEDIUM))
                .data(collection.coverPhoto?.getUrlByQuality(quality = PhotoQuality.MEDIUM))
                .crossfade(durationMillis = 1000)
                //.placeholder(placeholderBitmap?.toDrawable(context.resources))
                //.fallback(placeholderBitmap?.toDrawable(context.resources))
                /*.error(
                    ColorDrawable(
                        collectState.newCoverPhoto?.primaryColorInt ?: Color.Gray.toArgb()
                    )
                )*/
                .error(
                    ColorDrawable(
                        collection.coverPhoto?.primaryColorInt ?: Color.Gray.toArgb()
                    )
                )
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
    AlertDialog(
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
