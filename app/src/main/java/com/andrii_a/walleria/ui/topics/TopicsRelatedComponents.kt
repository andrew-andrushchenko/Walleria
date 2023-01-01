package com.andrii_a.walleria.ui.topics

import android.graphics.drawable.ColorDrawable
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.andrii_a.walleria.R
import com.andrii_a.walleria.core.TopicStatus
import com.andrii_a.walleria.domain.models.photo.Photo
import com.andrii_a.walleria.domain.models.topic.Topic
import com.andrii_a.walleria.ui.common.TopicInfo
import com.andrii_a.walleria.ui.theme.TopicStatusClosedTextColorDark
import com.andrii_a.walleria.ui.theme.TopicStatusClosedTextColorLight
import com.andrii_a.walleria.ui.theme.TopicStatusOpenTextColorDark
import com.andrii_a.walleria.ui.theme.TopicStatusOpenTextColorLight
import com.andrii_a.walleria.ui.util.abbreviatedNumberString
import com.andrii_a.walleria.ui.util.timeAgoLocalizedString
import com.andrii_a.walleria.ui.util.titleRes
import kotlinx.coroutines.flow.Flow
import java.util.*
import android.graphics.Color as AndroidColor
import androidx.compose.ui.graphics.Color as ComposeColor

@Composable
fun TopicsList(
    pagingDataFlow: Flow<PagingData<Topic>>,
    modifier: Modifier = Modifier,
    onClick: (TopicInfo) -> Unit,
    listState: LazyListState = rememberLazyListState(),
    contentPadding: PaddingValues = PaddingValues()
) {
    val lazyTopicsItems = pagingDataFlow.collectAsLazyPagingItems()

    LazyColumn(
        state = listState,
        contentPadding = contentPadding,
        modifier = modifier
    ) {
        items(lazyTopicsItems) { topic ->
            topic?.let {
                DefaultTopicItem(
                    title = topic.title.orEmpty(),
                    coverPhoto = topic.coverPhoto,
                    totalPhotos = topic.totalPhotos ?: 0,
                    curatorUsername = topic.owners?.first()?.username.orEmpty(),
                    status = topic.status,
                    updatedAt = topic.updatedAt.orEmpty(),
                    onClick = {
                        val topicInfo = TopicInfo(
                            idAsString = topic.id,
                            title = topic.title
                        )
                        onClick(topicInfo)
                    },
                    modifier = Modifier.padding(
                        start = 8.dp,
                        end = 8.dp,
                        bottom = 8.dp
                    )
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DefaultTopicItem(
    title: String,
    coverPhoto: Photo?,
    totalPhotos: Int,
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
            val (titleText, coverPhotoAsyncImage, statusText, curatorUsernameText, lastUpdatedText, totalPhotosText) = createRefs()

            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(coverPhoto?.urls?.regular.orEmpty())
                    .crossfade(durationMillis = 1000)
                    .placeholder(ColorDrawable(coverPhoto?.color?.let {
                        AndroidColor.parseColor(it)
                    } ?: AndroidColor.GRAY))
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
                style = MaterialTheme.typography.h6,
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
                style = MaterialTheme.typography.subtitle2,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.constrainAs(curatorUsernameText) {
                    top.linkTo(titleText.bottom, margin = 0.dp)
                    start.linkTo(coverPhotoAsyncImage.end, margin = 12.dp)
                    width = Dimension.fillToConstraints
                }
            )

            Text(
                text = stringResource(
                    id = R.string.topic_photos_formatted,
                    totalPhotos.abbreviatedNumberString
                ),
                style = MaterialTheme.typography.subtitle2,
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
                style = MaterialTheme.typography.subtitle1,
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
private fun StatusIndicatorText(status: TopicStatus, modifier: Modifier = Modifier) {
    Text(
        text = stringResource(id = status.titleRes),
        style = MaterialTheme.typography.subtitle2,
        maxLines = 1,
        color = status.color,
        overflow = TextOverflow.Ellipsis,
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(color = status.color.copy(alpha = 0.1f))
            .padding(8.dp)
    )
}

private val TopicStatus.color: ComposeColor
    @Composable
    get() = if (isSystemInDarkTheme()) {
        when (this) {
            TopicStatus.OPEN -> TopicStatusOpenTextColorDark
            TopicStatus.CLOSED -> TopicStatusClosedTextColorDark
            else -> MaterialTheme.colors.onPrimary
        }
    } else {
        when (this) {
            TopicStatus.OPEN -> TopicStatusOpenTextColorLight
            TopicStatus.CLOSED -> TopicStatusClosedTextColorLight
            else -> MaterialTheme.colors.onPrimary
        }
    }

