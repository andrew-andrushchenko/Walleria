package com.andrii_a.walleria.ui.common.components.lists

import android.content.res.Configuration
import android.graphics.drawable.ColorDrawable
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
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
import com.andrii_a.walleria.domain.models.photo.PhotoUrls
import com.andrii_a.walleria.domain.models.topic.Topic
import com.andrii_a.walleria.ui.common.TopicId
import com.andrii_a.walleria.ui.common.components.EmptyContentBanner
import com.andrii_a.walleria.ui.common.components.ErrorBanner
import com.andrii_a.walleria.ui.common.components.ErrorItem
import com.andrii_a.walleria.ui.common.components.LoadingListItem
import com.andrii_a.walleria.ui.common.components.ScrollToTopLayout
import com.andrii_a.walleria.ui.theme.PhotoDetailsActionButtonContainerColor
import com.andrii_a.walleria.ui.theme.PhotoDetailsActionButtonContentColor
import com.andrii_a.walleria.ui.theme.WalleriaTheme
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
    ConstraintLayout(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .fillMaxWidth()
            .height(150.dp)
            .clickable(onClick = onClick)
    ) {
        val (
            coverPhotoImage, titleText, statusChip,
            curatorUsernameText, lastUpdatedText, totalPhotosText
        ) = createRefs()

        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(coverPhoto?.getUrlByQuality(coverPhotoQuality))
                .crossfade(durationMillis = 1000)
                .placeholder(
                    ColorDrawable(
                        coverPhoto?.primaryColorInt ?: android.graphics.Color.GRAY
                    )
                )
                .build(),
            contentDescription = stringResource(id = R.string.topic_cover_photo),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .constrainAs(coverPhotoImage) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
                .drawWithContent {
                    drawContent()
                    drawRect(color = PhotoDetailsActionButtonContentColor.copy(alpha = 0.5f))
                }
        )

        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = PhotoDetailsActionButtonContainerColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.constrainAs(titleText) {
                top.linkTo(parent.top, 16.dp)
                start.linkTo(parent.start, 16.dp)
                end.linkTo(statusChip.start, 16.dp)
                width = Dimension.fillToConstraints
            }
        )

        Text(
            text = stringResource(id = R.string.topic_curated_by_formatted, curatorUsername),
            style = MaterialTheme.typography.titleSmall,
            color = PhotoDetailsActionButtonContainerColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.constrainAs(curatorUsernameText) {
                top.linkTo(titleText.bottom, 8.dp)
                start.linkTo(parent.start, 16.dp)
                width = Dimension.fillToConstraints
            }
        )

        Text(
            text = stringResource(
                id = R.string.topic_photos_formatted,
                totalPhotos.abbreviatedNumberString
            ),
            style = MaterialTheme.typography.titleMedium,
            color = PhotoDetailsActionButtonContainerColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.constrainAs(totalPhotosText) {
                bottom.linkTo(parent.bottom, 16.dp)
                start.linkTo(parent.start, 16.dp)
                end.linkTo(lastUpdatedText.start, 8.dp)
                width = Dimension.fillToConstraints
            }
        )

        Text(
            text = updatedAt.timeAgoLocalizedString.orEmpty(),
            style = MaterialTheme.typography.titleMedium,
            color = PhotoDetailsActionButtonContainerColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.constrainAs(lastUpdatedText) {
                end.linkTo(parent.end, 16.dp)
                bottom.linkTo(parent.bottom, 16.dp)
                width = Dimension.fillToConstraints
            }
        )

        StatusChip(
            status = status,
            modifier = Modifier
                .constrainAs(statusChip) {
                    end.linkTo(parent.end, 16.dp)
                    top.linkTo(parent.top, 4.dp)
                    width = Dimension.fillToConstraints
                }
        )
    }
}

@Composable
fun StatusChip(
    status: TopicStatus,
    modifier: Modifier = Modifier,
    color: Color = PhotoDetailsActionButtonContainerColor
) {
    SuggestionChip(
        label = { Text(text = stringResource(id = status.titleRes)) },
        onClick = {},
        colors = SuggestionChipDefaults.suggestionChipColors(
            labelColor = color
        ),
        border = SuggestionChipDefaults.suggestionChipBorder(
            borderColor = color
        ),
        modifier = modifier
    )
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun DefaultTopicItemPreview() {
    WalleriaTheme {
        val photo = Photo(
            id = "",
            width = 200,
            height = 300,
            color = "#E0E0E0",
            blurHash = "",
            views = 200,
            downloads = 200,
            likes = 10,
            likedByUser = false,
            description = "",
            exif = null,
            location = null,
            tags = null,
            relatedCollections = null,
            currentUserCollections = null,
            sponsorship = null,
            urls = PhotoUrls("", "", "", "", ""),
            links = null,
            user = null
        )

        DefaultTopicItem(
            title = "Topic title",
            coverPhoto = photo,
            coverPhotoQuality = PhotoQuality.MEDIUM,
            totalPhotos = 180_000,
            curatorUsername = "John Smith",
            status = TopicStatus.OPEN,
            updatedAt = "2023-09-13T10:39:35Z",
            onClick = {}
        )
    }
}
