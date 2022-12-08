package com.andrii_a.walleria.ui.collections

import android.graphics.drawable.ColorDrawable
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.andrii_a.walleria.R
import com.andrii_a.walleria.core.PhotoQuality
import com.andrii_a.walleria.domain.models.collection.Collection
import com.andrii_a.walleria.domain.models.photo.Photo
import com.andrii_a.walleria.ui.common.CollectionInfo
import com.andrii_a.walleria.ui.common.PhotoId
import com.andrii_a.walleria.ui.common.ScrollToTopLayout
import com.andrii_a.walleria.ui.common.UserNickname
import kotlinx.coroutines.flow.Flow

@Composable
fun CollectionsList(
    pagingDataFlow: Flow<PagingData<Collection>>,
    onCollectionClicked: (CollectionInfo) -> Unit,
    onUserProfileClicked: (UserNickname) -> Unit,
    onPhotoClicked: (PhotoId) -> Unit,
    modifier: Modifier = Modifier,
    photosPreviewQuality: PhotoQuality = PhotoQuality.MEDIUM,
    listState: LazyListState = rememberLazyListState(),
    contentPadding: PaddingValues = PaddingValues()
) {
    val lazyCollectionItems = pagingDataFlow.collectAsLazyPagingItems()

    ScrollToTopLayout(
        listState = listState,
        contentPadding = PaddingValues(bottom = 120.dp)
    ) {
        LazyColumn(
            state = listState,
            contentPadding = contentPadding,
            modifier = modifier
        ) {
            items(lazyCollectionItems) { collection ->
                collection?.let {
                    val previewPhotos = collection.previewPhotos?.take(3) ?: emptyList()

                    val onPhotoClickListeners = previewPhotos.map { photo ->
                        val listener: () -> Unit = {
                            onPhotoClicked(PhotoId(photo.id))
                        }
                        listener
                    }

                    DefaultCollectionItem(
                        title = collection.title,
                        previewPhotos = previewPhotos,
                        totalPhotos = collection.totalPhotos,
                        curatorUsername = collection.user?.username.orEmpty(),
                        onOpenCollectionClick = {
                            val collectionInfo = CollectionInfo(
                                idAsString = collection.id,
                                title = collection.title,
                                totalPhotos = collection.totalPhotos,
                                userNickname = collection.user?.username.orEmpty(),
                                userFullName = "${collection.user?.firstName.orEmpty()} ${collection.user?.lastName.orEmpty()}",
                                description = collection.description.orEmpty(),
                                isPrivate = collection.private ?: false
                            )
                            onCollectionClicked(collectionInfo)
                        },
                        onUserProfileClick = {
                            val userNickname = UserNickname(collection.user?.username.orEmpty())
                            onUserProfileClicked(userNickname)
                        },
                        onPhotoClickListeners = onPhotoClickListeners,
                        modifier = Modifier.padding(bottom = 32.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun DefaultCollectionItem(
    title: String,
    previewPhotos: List<Photo>,
    totalPhotos: Int,
    curatorUsername: String,
    modifier: Modifier = Modifier,
    onOpenCollectionClick: () -> Unit,
    onUserProfileClick: () -> Unit,
    onPhotoClickListeners: List<() -> Unit>
) {
    require(previewPhotos.size <= 3) { "This composable requires at most 3 photos" }

    BoxWithConstraints(modifier = modifier) {
        val constraints = this

        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            CollectionPhotosLayout(
                previewPhotos,
                onPhotoClickListeners,
                constraints
            )

            DefaultDetailsRow(
                title,
                curatorUsername,
                totalPhotos,
                onUserProfileClick,
                onOpenCollectionClick
            )
        }
    }
}

@Composable
private fun CollectionPhotosLayout(
    previewPhotos: List<Photo>,
    onPhotoClickListeners: List<() -> Unit>,
    constraints: BoxWithConstraintsScope
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        when (previewPhotos.size) {
            1 -> {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(previewPhotos[0].urls.regular)
                        .crossfade(true)
                        .placeholder(ColorDrawable(previewPhotos[0].color?.let {
                            android.graphics.Color.parseColor(it)
                        } ?: android.graphics.Color.GRAY))
                        .build(),
                    contentDescription = stringResource(id = R.string.description_first_photo),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .width(constraints.maxWidth)
                        .height(300.dp)
                        .padding(start = 8.dp, end = 8.dp, top = 8.dp)
                        .clip(RoundedCornerShape(36.dp))
                        .clickable(onClick = onPhotoClickListeners[0])
                )
            }
            2 -> {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(previewPhotos[0].urls.regular)
                        .crossfade(true)
                        .placeholder(ColorDrawable(previewPhotos[0].color?.let {
                            android.graphics.Color.parseColor(it)
                        } ?: android.graphics.Color.GRAY))
                        .build(),
                    contentDescription = stringResource(id = R.string.description_first_photo),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size((constraints.maxWidth.value * 0.5).dp - 8.dp)
                        .clip(RoundedCornerShape(36.dp))
                        .clickable(onClick = onPhotoClickListeners[0])
                )

                Spacer(modifier = Modifier.padding(start = 8.dp))

                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(previewPhotos[1].urls.regular)
                        .crossfade(true)
                        .placeholder(ColorDrawable(previewPhotos[1].color?.let {
                            android.graphics.Color.parseColor(it)
                        } ?: android.graphics.Color.GRAY))
                        .build(),
                    contentDescription = stringResource(id = R.string.description_second_photo),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size((constraints.maxWidth.value * 0.5).dp - 8.dp)
                        .clip(RoundedCornerShape(36.dp))
                        .clickable(onClick = onPhotoClickListeners[1])
                )

            }
            3 -> {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(previewPhotos[0].urls.regular)
                        .crossfade(true)
                        .placeholder(ColorDrawable(previewPhotos[0].color?.let {
                            android.graphics.Color.parseColor(it)
                        } ?: android.graphics.Color.GRAY))
                        .build(),
                    contentDescription = stringResource(id = R.string.description_first_photo),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size((constraints.maxWidth.value * 0.66).dp - 16.dp)
                        .clip(RoundedCornerShape(36.dp))
                        .clickable(onClick = onPhotoClickListeners[0])
                )
                Spacer(modifier = Modifier.padding(start = 8.dp))
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(previewPhotos[1].urls.regular)
                            .crossfade(true)
                            .placeholder(ColorDrawable(previewPhotos[1].color?.let {
                                android.graphics.Color.parseColor(it)
                            } ?: android.graphics.Color.GRAY))
                            .build(),
                        contentDescription = stringResource(id = R.string.description_second_photo),
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size((constraints.maxWidth.value * 0.33).dp - 12.dp)
                            .clip(RoundedCornerShape(36.dp))
                            .clickable(onClick = onPhotoClickListeners[1])
                    )

                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(previewPhotos[2].urls.regular)
                            .crossfade(true)
                            .placeholder(ColorDrawable(previewPhotos[2].color?.let {
                                android.graphics.Color.parseColor(
                                    it
                                )
                            } ?: android.graphics.Color.GRAY))
                            .build(),
                        contentDescription = stringResource(id = R.string.description_third_photo),
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size((constraints.maxWidth.value * 0.33).dp - 12.dp)
                            .clip(RoundedCornerShape(36.dp))
                            .clickable(onClick = onPhotoClickListeners[2])
                    )
                }
            }
        }
    }
}

@Composable
private fun DefaultDetailsRow(
    title: String,
    curatorUsername: String,
    totalPhotos: Int,
    onUserProfileClick: () -> Unit,
    onOpenCollectionClick: () -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp)
    ) {
        Column(verticalArrangement = Arrangement.SpaceBetween) {
            Text(
                text = title,
                style = MaterialTheme.typography.h6,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = stringResource(
                    id = R.string.bullet_template,
                    curatorUsername,
                    totalPhotos
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.clickable(onClick = onUserProfileClick)
            )

        }

        FloatingActionButton(
            onClick = onOpenCollectionClick,
            shape = RoundedCornerShape(16.dp),
            content = {
                Icon(
                    imageVector = Icons.Default.ArrowForwardIos,
                    contentDescription = stringResource(id = R.string.description_open_collection)
                )
            }
        )
    }
}
