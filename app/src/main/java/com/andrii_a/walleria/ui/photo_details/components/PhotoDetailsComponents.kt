package com.andrii_a.walleria.ui.photo_details.components

import android.graphics.drawable.ColorDrawable
import android.text.SpannableStringBuilder
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.text.italic
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.andrii_a.walleria.R
import com.andrii_a.walleria.core.PhotoQuality
import com.andrii_a.walleria.domain.models.collection.Collection
import com.andrii_a.walleria.domain.models.common.Tag
import com.andrii_a.walleria.domain.models.photo.PhotoExif
import com.andrii_a.walleria.ui.theme.PrimaryDark
import com.andrii_a.walleria.ui.theme.PrimaryLight
import com.andrii_a.walleria.ui.util.abbreviatedNumberString
import com.andrii_a.walleria.ui.util.formCameraNameOrEmpty
import com.andrii_a.walleria.ui.util.getUrlByQuality

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
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onUserClick)
            .padding(12.dp)
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
                .size(32.dp)
                .clip(CircleShape)
        )

        Text(
            text = username,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = PrimaryLight
        )
    }
}

@Composable
fun BigUserRow(
    userProfileImageUrl: String,
    username: String,
    onUserClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onUserClick)
            .padding(8.dp)
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
                .size(48.dp)
                .clip(CircleShape)
        )

        Text(
            text = username,
            style = MaterialTheme.typography.subtitle1,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun LocationRow(
    locationString: String,
    onLocationClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onLocationClick)
    ) {
        Icon(painter = painterResource(id = R.drawable.ic_location), contentDescription = null)

        Text(
            text = locationString,
            style = MaterialTheme.typography.subtitle2,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun DescriptionColumn(
    description: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = stringResource(id = R.string.description),
            style = MaterialTheme.typography.subtitle1,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.fillMaxWidth()
        )

        var isExpanded by remember { mutableStateOf(false) }

        Text(
            text = description,
            style = MaterialTheme.typography.subtitle2,
            maxLines = if (isExpanded) Int.MAX_VALUE else 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterHorizontally)
                .animateContentSize()
                .clip(RoundedCornerShape(16.dp))
                .clickable { isExpanded = !isExpanded }
        )
    }
}

@Composable
fun ExifItem(
    title: String,
    text: String,
    modifier: Modifier = Modifier,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.Start,
        modifier = modifier
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.subtitle2,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Text(
            text = text,
            style = MaterialTheme.typography.caption,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun ExifGrid(
    exif: PhotoExif?,
    resolution: String,
    modifier: Modifier = Modifier
) {
    val unknown = SpannableStringBuilder(stringResource(id = R.string.unknown))

    val gridItems: List<Pair<String, SpannableStringBuilder>> = listOf(
        Pair(
            stringResource(id = R.string.camera),
            exif?.let {
                SpannableStringBuilder().append(
                    it.formCameraNameOrEmpty().ifEmpty { unknown })
            } ?: unknown
        ),
        Pair(
            stringResource(id = R.string.aperture),
            exif?.aperture?.let { SpannableStringBuilder().italic { append("f") }.append("/$it") }
                ?: unknown
        ),
        Pair(
            stringResource(id = R.string.focal_length),
            exif?.focalLength?.let { SpannableStringBuilder("${it}mm") } ?: unknown
        ),
        Pair(
            stringResource(id = R.string.shutter_speed),
            exif?.exposureTime?.let { SpannableStringBuilder("${it}s") } ?: unknown
        ),
        Pair(
            stringResource(id = R.string.iso),
            exif?.iso?.let { SpannableStringBuilder(it.toString()) } ?: unknown
        ),
        Pair(
            stringResource(id = R.string.resolution),
            SpannableStringBuilder(resolution)
        )
    )

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier.height(180.dp)
    ) {
        items(gridItems) { (categoryName, categoryValue) ->
            ExifItem(title = categoryName, text = categoryValue.toString())
        }
    }
}

@Composable
fun TagItem(
    modifier: Modifier = Modifier,
    title: String,
    onClick: (String) -> Unit
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .border(
                width = 1.dp,
                shape = RoundedCornerShape(50),
                color = MaterialTheme.colors.onPrimary
            )
            .clickable { onClick(title) }
            .padding(8.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.subtitle2,
            modifier = Modifier.padding(horizontal = 4.dp)
        )
    }
}

@Composable
fun TagsRow(
    tags: List<Tag>,
    onTagClicked: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(modifier = modifier) {
        itemsIndexed(tags) { index, item ->
            TagItem(
                title = item.title,
                onClick = onTagClicked,
                modifier = Modifier.padding(
                    start = if (index == 0) 8.dp else 0.dp,
                    end = 8.dp
                )
            )
        }
    }
}

@Composable
fun StatsItem(
    @DrawableRes drawableRes: Int,
    @StringRes titleRes: Int,
    value: Long,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.padding(16.dp)) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(painter = painterResource(id = drawableRes), contentDescription = null)

                Text(
                    text = stringResource(id = titleRes),
                    style = MaterialTheme.typography.subtitle2,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.padding(vertical = 4.dp))

            Text(
                text = value.abbreviatedNumberString,
                style = MaterialTheme.typography.subtitle2,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun StatsRow(
    views: Long,
    likes: Long,
    downloads: Long,
    modifier: Modifier = Modifier,
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        StatsItem(
            drawableRes = R.drawable.ic_view_outlined,
            titleRes = R.string.views,
            value = views,
            modifier = Modifier.weight(0.33f)
        )

        Spacer(modifier = Modifier.padding(horizontal = 4.dp))

        StatsItem(
            drawableRes = R.drawable.ic_like_outlined,
            titleRes = R.string.likes,
            value = likes,
            modifier = Modifier.weight(0.33f)
        )

        Spacer(modifier = Modifier.padding(horizontal = 4.dp))

        StatsItem(
            drawableRes = R.drawable.ic_download_outlined,
            titleRes = R.string.downloads,
            value = downloads,
            modifier = Modifier.weight(0.33f)
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun RelatedCollectionsItem(
    title: String,
    coverPhotoUrl: String,
    totalPhotos: Long,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        onClick = {},
        modifier = modifier
    ) {
        Box(
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = ImageRequest
                    .Builder(LocalContext.current)
                    .data(coverPhotoUrl)
                    .crossfade(durationMillis = 1000)
                    .placeholder(ColorDrawable(Color.Gray.toArgb()))
                    .build(),
                contentScale = ContentScale.Crop,
                contentDescription = null,
                modifier = Modifier.size(150.dp)
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .size(150.dp)
                    .background(color = PrimaryDark.copy(alpha = 0.5f))
                    .padding(horizontal = 8.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.subtitle1,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = PrimaryLight
                )

                Text(
                    text = stringResource(
                        id = R.string.topic_photos_formatted,
                        totalPhotos.abbreviatedNumberString
                    ),
                    style = MaterialTheme.typography.subtitle2,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = PrimaryLight
                )
            }
        }
    }

}

@Composable
fun RelatedCollectionsRow(
    collections: List<Collection>,
    modifier: Modifier = Modifier
) {
    LazyRow(modifier = modifier) {
        itemsIndexed(collections) { index, item ->
            RelatedCollectionsItem(
                title = item.title,
                coverPhotoUrl = item.coverPhoto?.getUrlByQuality(PhotoQuality.MEDIUM).orEmpty(),
                totalPhotos = item.totalPhotos,
                modifier = Modifier.padding(
                    start = if (index == 0) 8.dp else 0.dp,
                    end = 8.dp
                )
            )
        }
    }
}