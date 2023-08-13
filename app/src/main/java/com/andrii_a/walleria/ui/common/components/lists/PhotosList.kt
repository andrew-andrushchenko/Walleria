package com.andrii_a.walleria.ui.common.components.lists

import android.graphics.drawable.ColorDrawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.andrii_a.walleria.R
import com.andrii_a.walleria.domain.PhotoQuality
import com.andrii_a.walleria.domain.models.photo.Photo
import com.andrii_a.walleria.ui.common.PhotoId
import com.andrii_a.walleria.ui.common.UserNickname
import com.andrii_a.walleria.ui.common.components.EmptyContentBanner
import com.andrii_a.walleria.ui.common.components.ErrorBanner
import com.andrii_a.walleria.ui.common.components.ErrorItem
import com.andrii_a.walleria.ui.common.components.LoadingListItem
import com.andrii_a.walleria.ui.util.getUrlByQuality
import com.andrii_a.walleria.ui.util.getUserProfileImageUrlOrEmpty
import com.andrii_a.walleria.ui.util.primaryColorComposable
import com.andrii_a.walleria.ui.util.userFullName
import com.andrii_a.walleria.ui.util.userNickname

@Composable
fun PhotosList(
    lazyPhotoItems: LazyPagingItems<Photo>,
    onPhotoClicked: (PhotoId) -> Unit,
    onUserProfileClicked: (UserNickname) -> Unit,
    modifier: Modifier = Modifier,
    photosQuality: PhotoQuality = PhotoQuality.MEDIUM,
    listState: LazyListState = rememberLazyListState(),
    contentPadding: PaddingValues = PaddingValues(),
    headerContent: (@Composable () -> Unit)? = null
) {
    LazyColumn(
        state = listState,
        contentPadding = contentPadding,
        modifier = modifier
    ) {
        when (lazyPhotoItems.loadState.refresh) {
            is LoadState.NotLoading -> {
                headerContent?.let { header ->
                    item {
                        header.invoke()
                    }
                }

                if (lazyPhotoItems.itemCount > 0) {
                    items(
                        count = lazyPhotoItems.itemCount,
                        key = lazyPhotoItems.itemKey { it.id }
                    ) { index ->
                        val photo = lazyPhotoItems[index]
                        photo?.let {
                            DefaultPhotoItem(
                                width = photo.width.toFloat(),
                                height = photo.height.toFloat(),
                                photoUrl = photo.getUrlByQuality(photosQuality),
                                photoPlaceholderColor = photo.primaryColorComposable,
                                userProfileImageUrl = photo.getUserProfileImageUrlOrEmpty(),
                                username = photo.userFullName,
                                onPhotoClicked = { onPhotoClicked(PhotoId(it.id)) },
                                onUserClick = { onUserProfileClicked(UserNickname(photo.userNickname)) },
                                modifier = Modifier
                                    .padding(
                                        start = 16.dp,
                                        end = 16.dp,
                                        bottom = 16.dp
                                    )
                            )
                        }
                    }
                } else {
                    item {
                        EmptyContentBanner(modifier = Modifier.fillParentMaxSize())
                    }
                }
            }

            is LoadState.Loading -> Unit

            is LoadState.Error -> {
                item {
                    ErrorBanner(
                        onRetry = lazyPhotoItems::retry,
                        modifier = Modifier.fillParentMaxSize()
                    )
                }
            }
        }

        when (lazyPhotoItems.loadState.append) {
            is LoadState.NotLoading -> Unit

            is LoadState.Loading -> {
                item {
                    LoadingListItem(modifier = Modifier.fillParentMaxWidth())
                }
            }

            is LoadState.Error -> {
                item {
                    ErrorItem(
                        onRetry = lazyPhotoItems::retry,
                        modifier = Modifier
                            .fillParentMaxWidth()
                            .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun DefaultPhotoItem(
    width: Float,
    height: Float,
    photoUrl: String,
    photoPlaceholderColor: Color,
    userProfileImageUrl: String,
    username: String,
    onPhotoClicked: () -> Unit,
    onUserClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        UserRow(
            userProfileImageUrl = userProfileImageUrl,
            username = username,
            onUserClick = onUserClick
        )

        val painter = rememberAsyncImagePainter(
            model = ImageRequest.Builder(LocalContext.current)
                .data(photoUrl)
                .crossfade(durationMillis = 1000)
                .placeholder(ColorDrawable(photoPlaceholderColor.toArgb()))
                .error(ColorDrawable(photoPlaceholderColor.toArgb()))
                .build(),
            contentScale = ContentScale.Fit
        )

        AspectRatioImage(
            width = width,
            height = height,
            description = stringResource(id = R.string.photo),
            painter = painter,
            onClick = onPhotoClicked
        )
    }
}

@Composable
fun AspectRatioImage(
    width: Float,
    height: Float,
    description: String,
    painter: Painter,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(16.dp),
    clickable: Boolean = true,
    onClick: () -> Unit = {}
) {
    Box(modifier = modifier) {
        val aspectRatio = width / height
        val imageModifier = if (clickable) {
            Modifier
                .aspectRatio(aspectRatio)
                .fillMaxWidth()
                .clip(shape)
                .clickable(onClick = onClick)
        } else {
            Modifier
                .aspectRatio(aspectRatio)
                .fillMaxWidth()
                .clip(shape)
        }

        Image(
            painter = painter,
            contentDescription = description,
            contentScale = ContentScale.Fit,
            modifier = imageModifier
        )
    }
}

@Composable
fun UserRow(
    userProfileImageUrl: String,
    username: String,
    onUserClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(12.dp)
            .clickable(onClick = onUserClick)
    ) {
        val painter = rememberAsyncImagePainter(
            model = ImageRequest.Builder(LocalContext.current)
                .data(userProfileImageUrl)
                .crossfade(durationMillis = 1000)
                .placeholder(ColorDrawable(Color.Gray.toArgb()))
                .build(),
            contentScale = ContentScale.Fit
        )

        Image(
            painter = painter,
            contentDescription = stringResource(id = R.string.user_profile_image),
            modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
        )

        Text(
            text = username,
            style = MaterialTheme.typography.subtitle1,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}