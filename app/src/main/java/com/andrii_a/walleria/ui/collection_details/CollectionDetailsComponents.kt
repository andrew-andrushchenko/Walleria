package com.andrii_a.walleria.ui.collection_details

import android.graphics.drawable.ColorDrawable
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.andrii_a.walleria.R
import com.andrii_a.walleria.domain.PhotoQuality
import com.andrii_a.walleria.domain.models.photo.Photo
import com.andrii_a.walleria.domain.models.user.User
import com.andrii_a.walleria.ui.common.PhotoId
import com.andrii_a.walleria.ui.common.UserNickname
import com.andrii_a.walleria.ui.common.components.EmptyContentBanner
import com.andrii_a.walleria.ui.common.components.ErrorBanner
import com.andrii_a.walleria.ui.common.components.ErrorItem
import com.andrii_a.walleria.ui.common.components.LoadingListItem
import com.andrii_a.walleria.ui.common.components.lists.DefaultPhotoItem
import com.andrii_a.walleria.ui.util.abbreviatedNumberString
import com.andrii_a.walleria.ui.util.getUrlByQuality
import com.andrii_a.walleria.ui.util.getUserProfileImageUrlOrEmpty
import com.andrii_a.walleria.ui.util.primaryColorComposable
import com.andrii_a.walleria.ui.util.userFullName
import com.andrii_a.walleria.ui.util.userNickname

@Composable
fun CollectionDetailsList(
    owner: User?,
    description: String?,
    totalPhotos: Long,
    modifier: Modifier = Modifier,
    lazyPhotoItems: LazyPagingItems<Photo>,
    onPhotoClicked: (PhotoId) -> Unit,
    onUserProfileClicked: (UserNickname) -> Unit,
    photosQuality: PhotoQuality = PhotoQuality.MEDIUM,
    listState: LazyListState = rememberLazyListState(),
    contentPadding: PaddingValues = PaddingValues()
) {
    LazyColumn(
        state = listState,
        contentPadding = contentPadding,
        modifier = modifier
    ) {
        when (lazyPhotoItems.loadState.refresh) {
            is LoadState.NotLoading -> {
                if (lazyPhotoItems.itemCount > 0) {
                    item {
                        DescriptionHeader(
                            owner = owner,
                            description = description,
                            totalPhotos = totalPhotos,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp, vertical = 16.dp)
                        )
                    }

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
private fun UserRowWithPhotoCount(
    user: User?,
    totalPhotos: Long,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
    ) {
        AsyncImage(
            model = ImageRequest
                .Builder(LocalContext.current)
                .data(user?.profileImage?.medium)
                .crossfade(durationMillis = 1000)
                .placeholder(ColorDrawable(Color.Gray.toArgb()))
                .build(),
            contentScale = ContentScale.Crop,
            contentDescription = null,
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
        )

        Text(
            text = stringResource(
                id = R.string.bullet_template,
                user?.userFullName.orEmpty(),
                totalPhotos.abbreviatedNumberString
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun DescriptionHeader(
    owner: User?,
    description: String?,
    totalPhotos: Long,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        description?.let {
            var expanded by remember {
                mutableStateOf(false)
            }

            Text(
                text = it,
                style = MaterialTheme.typography.subtitle1,
                color = MaterialTheme.colors.onPrimary,
                maxLines = if (expanded) Int.MAX_VALUE else 4,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .animateContentSize()
                    .clickable { expanded = !expanded }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        UserRowWithPhotoCount(
            user = owner,
            totalPhotos = totalPhotos,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}
