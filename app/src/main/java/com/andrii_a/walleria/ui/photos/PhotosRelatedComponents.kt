package com.andrii_a.walleria.ui.photos

import android.graphics.drawable.ColorDrawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.runtime.*
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
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.andrii_a.walleria.R
import com.andrii_a.walleria.core.PhotoListDisplayOrder
import com.andrii_a.walleria.core.PhotoQuality
import com.andrii_a.walleria.domain.models.photo.Photo
import com.andrii_a.walleria.ui.common.PhotoId
import com.andrii_a.walleria.ui.common.ScrollToTopLayout
import com.andrii_a.walleria.ui.common.UserNickname
import com.andrii_a.walleria.ui.util.titleRes
import kotlinx.coroutines.flow.Flow

@Composable
fun PhotosList(
    pagingDataFlow: Flow<PagingData<Photo>>,
    onPhotoClicked: (PhotoId) -> Unit,
    onUserProfileClicked: (UserNickname) -> Unit,
    modifier: Modifier = Modifier,
    photosQuality: PhotoQuality = PhotoQuality.MEDIUM,
    listState: LazyListState = rememberLazyListState(),
    contentPadding: PaddingValues = PaddingValues()
) {
    val lazyPhotoItems = pagingDataFlow.collectAsLazyPagingItems()

    ScrollToTopLayout(
        listState = listState,
        contentPadding = PaddingValues(bottom = 120.dp)
    ) {
        LazyColumn(
            state = listState,
            contentPadding = contentPadding,
            modifier = modifier
        ) {
            items(lazyPhotoItems) { photo ->
                photo?.let {
                    DefaultPhotoItem(
                        width = it.width.toFloat(),
                        height = it.height.toFloat(),
                        photoUrl = it.urls.regular, // TODO: replace later
                        photoPlaceholderColor = Color.Gray, // TODO: replace later
                        userProfileImageUrl = it.user?.profileImage?.medium.orEmpty(),
                        username = "${it.user?.firstName.orEmpty()} ${it.user?.lastName.orEmpty()}",
                        onPhotoClicked = { onPhotoClicked(PhotoId(it.id)) },
                        onUserClick = { onUserProfileClicked(UserNickname(photo.user?.username.orEmpty())) },
                        modifier = Modifier.padding(
                            start = 16.dp,
                            end = 16.dp,
                            bottom = 16.dp
                        )
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
                .crossfade(true)
                .placeholder(ColorDrawable(photoPlaceholderColor.toArgb()))
                .build(),
            contentScale = ContentScale.Fit
        )

        AspectRatioImage(
            width = width,
            height = height,
            description = "",
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
    shape: Shape = RoundedCornerShape(36.dp),
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
                .crossfade(true)
                .placeholder(ColorDrawable(Color.Gray.toArgb())) // TODO: replace later
                .build(),
            contentScale = ContentScale.Fit
        )

        Image(
            painter = painter,
            contentDescription = "User profile image",
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

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun TitleDropdown(
    title: String,
    orderPhotosBy: (Int) -> Unit
) {
    var dropdownExpanded by remember {
        mutableStateOf(false)
    }

    ExposedDropdownMenuBox(
        expanded = dropdownExpanded,
        onExpandedChange = {
            dropdownExpanded = !dropdownExpanded
        },
        modifier = Modifier.wrapContentWidth()
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.h6,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

            Icon(
                imageVector = if (dropdownExpanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                contentDescription = ""
            )
        }

        ExposedDropdownMenu(
            expanded = dropdownExpanded,
            onDismissRequest = {
                dropdownExpanded = false
            },
            modifier = Modifier.wrapContentWidth()
        ) {
            PhotoListDisplayOrder.values().forEach { orderOption ->
                DropdownMenuItem(
                    onClick = {
                        orderPhotosBy(orderOption.ordinal)
                        dropdownExpanded = false
                    }
                ) {
                    Text(
                        text = stringResource(
                            id = R.string.photos_title_template,
                            stringResource(id = orderOption.titleRes)
                        )
                    )
                }
            }
        }
    }
}
