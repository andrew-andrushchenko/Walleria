package com.andrii_a.walleria.ui.common.components

import android.content.res.Configuration
import android.graphics.Bitmap
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
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
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.andrii_a.walleria.R
import com.andrii_a.walleria.domain.PhotoQuality
import com.andrii_a.walleria.domain.TopicStatus
import com.andrii_a.walleria.domain.models.photo.Photo
import com.andrii_a.walleria.domain.models.photo.PhotoUrls
import com.andrii_a.walleria.domain.models.topic.Topic
import com.andrii_a.walleria.domain.models.user.User
import com.andrii_a.walleria.domain.models.user.UserSocialMediaLinks
import com.andrii_a.walleria.ui.common.TopicId
import com.andrii_a.walleria.ui.theme.PhotoDetailsActionButtonContainerColor
import com.andrii_a.walleria.ui.theme.PhotoDetailsActionButtonContentColor
import com.andrii_a.walleria.ui.theme.WalleriaTheme
import com.andrii_a.walleria.ui.util.BlurHashDecoder
import com.andrii_a.walleria.ui.util.abbreviatedNumberString
import com.andrii_a.walleria.ui.util.getUrlByQuality
import com.andrii_a.walleria.ui.util.ownerUsername
import com.andrii_a.walleria.ui.util.primaryColorInt
import com.andrii_a.walleria.ui.util.timeAgoLocalizedString
import com.andrii_a.walleria.ui.util.titleRes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID

@Composable
fun TopicsStaggeredGrid(
    lazyTopicItems: LazyPagingItems<Topic>,
    modifier: Modifier = Modifier,
    onClick: (TopicId) -> Unit,
    coverPhotoQuality: PhotoQuality = PhotoQuality.MEDIUM,
    gridState: LazyStaggeredGridState = rememberLazyStaggeredGridState(),
    contentPadding: PaddingValues = PaddingValues()
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Adaptive(250.dp),
            state = gridState,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalItemSpacing = 8.dp,
            contentPadding = contentPadding,
            modifier = modifier
        ) {
            if (lazyTopicItems.loadState.refresh is LoadState.NotLoading && lazyTopicItems.itemCount > 0) {
                items(
                    count = lazyTopicItems.itemCount,
                    key = lazyTopicItems.itemKey { it.id + UUID.randomUUID() }
                ) { index ->
                    val topic = lazyTopicItems[index]
                    topic?.let {
                        DefaultTopicItem(
                            topic = topic,
                            coverPhotoQuality = coverPhotoQuality,
                            onClick = { onClick(topic.id) },
                            modifier = Modifier.animateItem()
                        )
                    }
                }
            }

            when (lazyTopicItems.loadState.append) {
                is LoadState.NotLoading -> Unit

                is LoadState.Loading -> {
                    item(span = StaggeredGridItemSpan.FullLine) {
                        LoadingListItem(modifier = Modifier.fillMaxWidth())
                    }
                }

                is LoadState.Error -> {
                    item(span = StaggeredGridItemSpan.FullLine) {
                        ErrorItem(
                            onRetry = lazyTopicItems::retry,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                        )
                    }
                }
            }
        }

        if (lazyTopicItems.loadState.refresh is LoadState.Error) {
            ErrorBanner(
                onRetry = lazyTopicItems::retry,
                modifier = Modifier.fillMaxSize()
            )
        }

        if (lazyTopicItems.loadState.refresh is LoadState.Loading) {
            LoadingListItem(modifier = Modifier.fillMaxSize())
        }

        val shouldShowEmptyContent = lazyTopicItems.loadState.refresh is LoadState.NotLoading
                && lazyTopicItems.itemCount == 0

        if (shouldShowEmptyContent) {
            EmptyContentBanner(modifier = Modifier.fillMaxSize())
        }
    }
}

@Composable
fun DefaultTopicItem(
    topic: Topic,
    coverPhotoQuality: PhotoQuality,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val context = LocalContext.current

    val placeholderBitmap by produceState<Bitmap?>(initialValue = null) {
        value = withContext(Dispatchers.Default) {
            BlurHashDecoder.decode(
                blurHash = topic.coverPhoto?.blurHash,
                width = 4,
                height = 3
            )
        }
    }

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
                .data(topic.coverPhoto?.getUrlByQuality(coverPhotoQuality))
                .crossfade(durationMillis = 1000)
                .placeholder(placeholderBitmap?.toDrawable(context.resources))
                .fallback(placeholderBitmap?.toDrawable(context.resources))
                .error((topic.coverPhoto?.primaryColorInt ?: Color.Gray.toArgb()).toDrawable())
                .build(),
            contentDescription = stringResource(id = R.string.topic_cover_photo),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
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
            text = topic.title,
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
            text = stringResource(id = R.string.topic_curated_by_formatted, topic.ownerUsername),
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
                topic.totalPhotos.abbreviatedNumberString
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
            text = topic.updatedAt.timeAgoLocalizedString.orEmpty(),
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
            status = topic.status,
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
        border = BorderStroke(1.dp, color),
        modifier = modifier
    )
}

@Composable
fun TopicsGridContent(
    topicItems: LazyPagingItems<Topic>,
    modifier: Modifier = Modifier,
    gridState: LazyStaggeredGridState = rememberLazyStaggeredGridState(),
    contentPadding: PaddingValues = PaddingValues(),
    scrollToTopButtonPadding: PaddingValues = PaddingValues(
        bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding() + 8.dp
    ),
    onTopicClick: (TopicId) -> Unit,
) {
    ScrollToTopLayout(
        gridState = gridState,
        scrollToTopButtonPadding = scrollToTopButtonPadding,
        modifier = modifier
    ) {
        TopicsStaggeredGrid(
            lazyTopicItems = topicItems,
            onClick = onTopicClick,
            gridState = gridState,
            contentPadding = PaddingValues(
                top = contentPadding.calculateTopPadding() + 16.dp,
                bottom = contentPadding.calculateBottomPadding() /*+ 150.dp*/,
                start = 16.dp,
                end = 16.dp
            ),
            modifier = Modifier.fillMaxSize()
        )
    }
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
            blurHash = "LFC\$yHwc8^\$yIAS\$%M%00KxukYIp",
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

        val user = User(
            id = "",
            username = "johny_smith",
            firstName = "John",
            lastName = "Smith",
            bio = "",
            location = "",
            totalLikes = 0,
            totalPhotos = 0,
            totalCollections = 0,
            followersCount = 0,
            followingCount = 0,
            downloads = 0,
            profileImage = null,
            social = UserSocialMediaLinks(
                instagramUsername = "abc",
                portfolioUrl = "abc",
                twitterUsername = "abc",
                paypalEmail = "abc"
            ),
            tags = null,
            photos = null
        )

        val topic = Topic(
            id = "",
            title = "Topic title",
            description = "",
            featured = false,
            startsAt = "",
            endsAt = "",
            updatedAt = "2023-09-13T10:39:35Z",
            totalPhotos = 100_000,
            links = null,
            status = TopicStatus.OPEN,
            owners = listOf(user),
            coverPhoto = photo,
            previewPhotos = null
        )

        DefaultTopicItem(
            topic = topic,
            coverPhotoQuality = PhotoQuality.MEDIUM,
            onClick = {}
        )
    }
}
