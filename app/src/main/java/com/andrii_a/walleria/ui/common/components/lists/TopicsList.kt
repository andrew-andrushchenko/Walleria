package com.andrii_a.walleria.ui.common.components.lists

import android.graphics.drawable.ColorDrawable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.andrii_a.walleria.R
import com.andrii_a.walleria.domain.PhotoQuality
import com.andrii_a.walleria.domain.TopicStatus
import com.andrii_a.walleria.domain.models.photo.Photo
import com.andrii_a.walleria.domain.models.topic.Topic
import com.andrii_a.walleria.ui.common.TopicId
import com.andrii_a.walleria.ui.common.components.EmptyContentBanner
import com.andrii_a.walleria.ui.common.components.ErrorBanner
import com.andrii_a.walleria.ui.common.components.ErrorItem
import com.andrii_a.walleria.ui.common.components.LoadingListItem
import com.andrii_a.walleria.ui.common.components.ScrollToTopLayout
import com.andrii_a.walleria.ui.util.abbreviatedNumberString
import com.andrii_a.walleria.ui.util.getUrlByQuality
import com.andrii_a.walleria.ui.util.ownerUsername
import com.andrii_a.walleria.ui.util.primaryColorInt
import com.andrii_a.walleria.ui.util.timeAgoLocalizedString
import com.andrii_a.walleria.ui.util.titleRes

@Composable
fun TopicsList(
    lazyTopicItems: LazyPagingItems<Topic>,
    modifier: Modifier = Modifier,
    onClick: (TopicId) -> Unit,
    addNavigationBarPadding: Boolean = false,
    coverPhotoQuality: PhotoQuality = PhotoQuality.MEDIUM,
    listState: LazyListState = rememberLazyListState(),
    contentPadding: PaddingValues = PaddingValues()
) {
    ScrollToTopLayout(
        listState = listState,
        scrollToTopButtonPadding = PaddingValues(
            bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding() +
                    dimensionResource(id = R.dimen.scroll_to_top_button_padding) +
                    if (addNavigationBarPadding) {
                        dimensionResource(id = R.dimen.navigation_bar_height)
                    } else {
                        0.dp
                    }
        )
    ) {
        LazyColumn(
            state = listState,
            contentPadding = contentPadding,
            modifier = modifier
        ) {
            when (lazyTopicItems.loadState.refresh) {
                is LoadState.NotLoading -> {
                    if (lazyTopicItems.itemCount > 0) {
                        items(
                            count = lazyTopicItems.itemCount,
                            key = lazyTopicItems.itemKey { it.id }
                        ) { index ->
                            val topic = lazyTopicItems[index]
                            topic?.let {
                                DefaultTopicItem(
                                    title = topic.title,
                                    coverPhoto = topic.coverPhoto,
                                    coverPhotoQuality = coverPhotoQuality,
                                    totalPhotos = topic.totalPhotos,
                                    curatorUsername = topic.ownerUsername,
                                    status = topic.status,
                                    updatedAt = topic.updatedAt.orEmpty(),
                                    onClick = {
                                        onClick(TopicId(topic.id))
                                    },
                                    modifier = Modifier.padding(
                                        start = 8.dp,
                                        end = 8.dp,
                                        bottom = 8.dp
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
                            onRetry = lazyTopicItems::retry,
                            modifier = Modifier.fillParentMaxSize()
                        )
                    }
                }
            }

            when (lazyTopicItems.loadState.append) {
                is LoadState.NotLoading -> Unit

                is LoadState.Loading -> {
                    item {
                        LoadingListItem(modifier = Modifier.fillParentMaxWidth())
                    }
                }

                is LoadState.Error -> {
                    item {
                        ErrorItem(
                            onRetry = lazyTopicItems::retry,
                            modifier = Modifier
                                .fillParentMaxWidth()
                                .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                        )
                    }
                }
            }
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DefaultTopicItem(
    title: String,
    coverPhoto: Photo?,
    coverPhotoQuality: PhotoQuality,
    totalPhotos: Long,
    curatorUsername: String,
    status: TopicStatus,
    updatedAt: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        onClick = onClick,
        modifier = modifier
    ) {
        ConstraintLayout(modifier = Modifier.fillMaxWidth()) {
            val (
                titleText, coverPhotoAsyncImage, statusText,
                curatorUsernameText, lastUpdatedText, totalPhotosText
            ) = createRefs()

            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(coverPhoto?.getUrlByQuality(coverPhotoQuality))
                    .crossfade(durationMillis = 1000)
                    .placeholder(ColorDrawable(coverPhoto?.primaryColorInt ?: android.graphics.Color.GRAY))
                    .build(),
                contentDescription = stringResource(id = R.string.topic_cover_photo),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .constrainAs(coverPhotoAsyncImage) {
                        top.linkTo(parent.top, margin = 12.dp)
                        start.linkTo(parent.start, margin = 12.dp)
                        end.linkTo(titleText.start, margin = 12.dp)
                        bottom.linkTo(parent.bottom, margin = 12.dp)
                    }
                    .size(120.dp)
                    .clip(RoundedCornerShape(12.dp))
            )

            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.constrainAs(titleText) {
                    top.linkTo(parent.top, margin = 12.dp)
                    start.linkTo(coverPhotoAsyncImage.end)
                    end.linkTo(statusText.start, margin = 12.dp)
                    width = Dimension.fillToConstraints
                }
            )

            Text(
                text = stringResource(id = R.string.topic_curated_by_formatted, curatorUsername),
                style = MaterialTheme.typography.titleSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.constrainAs(curatorUsernameText) {
                    top.linkTo(titleText.bottom)
                    start.linkTo(coverPhotoAsyncImage.end, margin = 12.dp)
                    width = Dimension.fillToConstraints
                }
            )

            Text(
                text = stringResource(
                    id = R.string.topic_photos_formatted,
                    totalPhotos.abbreviatedNumberString
                ),
                style = MaterialTheme.typography.titleSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.constrainAs(totalPhotosText) {
                    top.linkTo(curatorUsernameText.bottom, margin = 12.dp)
                    start.linkTo(coverPhotoAsyncImage.end, margin = 12.dp)
                    width = Dimension.fillToConstraints
                }
            )

            Text(
                text = updatedAt.timeAgoLocalizedString.orEmpty(),
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.constrainAs(lastUpdatedText) {
                    end.linkTo(parent.end, margin = 12.dp)
                    bottom.linkTo(parent.bottom, margin = 12.dp)
                    width = Dimension.fillToConstraints
                }
            )

            StatusIndicatorText(
                status = status,
                modifier = Modifier
                    .constrainAs(statusText) {
                        end.linkTo(parent.end, margin = 12.dp)
                        top.linkTo(parent.top, margin = 12.dp)
                        width = Dimension.fillToConstraints
                    }
            )
        }
    }
}

@Composable
fun StatusIndicatorText(status: TopicStatus, modifier: Modifier = Modifier) {
    Text(
        text = stringResource(id = status.titleRes),
        maxLines = 1,
        color = MaterialTheme.colorScheme.secondaryContainer,
        overflow = TextOverflow.Ellipsis,
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(color = MaterialTheme.colorScheme.secondary)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    )
}
