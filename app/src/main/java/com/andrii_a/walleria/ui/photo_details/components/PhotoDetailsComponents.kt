package com.andrii_a.walleria.ui.photo_details.components

import android.graphics.Bitmap
import android.graphics.drawable.ColorDrawable
import android.text.SpannableStringBuilder
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.FileDownload
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.RemoveRedEye
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toDrawable
import androidx.core.text.italic
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.andrii_a.walleria.R
import com.andrii_a.walleria.domain.PhotoQuality
import com.andrii_a.walleria.domain.models.collection.Collection
import com.andrii_a.walleria.domain.models.photo.PhotoExif
import com.andrii_a.walleria.ui.common.CollectionId
import com.andrii_a.walleria.ui.util.BlurHashDecoder
import com.andrii_a.walleria.ui.util.abbreviatedNumberString
import com.andrii_a.walleria.ui.util.formCameraNameOrEmpty
import com.andrii_a.walleria.ui.util.getUrlByQuality
import com.andrii_a.walleria.ui.util.primaryColorInt
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

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
            color = Color.White
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
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onLocationClick)
    ) {
        Icon(imageVector = Icons.Outlined.LocationOn, contentDescription = null)

        Text(
            text = locationString,
            style = MaterialTheme.typography.titleMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
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
            style = MaterialTheme.typography.titleSmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
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
        userScrollEnabled = false,
        modifier = modifier.requiredHeight(120.dp)
    ) {
        items(gridItems) { (categoryName, categoryValue) ->
            ExifItem(title = categoryName, text = categoryValue.toString())
        }
    }
}

@Composable
fun StatsItem(
    imageVector: ImageVector,
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
                Icon(imageVector = imageVector, contentDescription = null)

                Text(
                    text = stringResource(id = titleRes),
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.padding(vertical = 4.dp))

            Text(
                text = value.abbreviatedNumberString,
                style = MaterialTheme.typography.titleSmall,
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
            imageVector = Icons.Outlined.RemoveRedEye,
            titleRes = R.string.views,
            value = views,
            modifier = Modifier.weight(0.33f)
        )

        Spacer(modifier = Modifier.padding(horizontal = 4.dp))

        StatsItem(
            imageVector = Icons.Outlined.FavoriteBorder,
            titleRes = R.string.likes,
            value = likes,
            modifier = Modifier.weight(0.33f)
        )

        Spacer(modifier = Modifier.padding(horizontal = 4.dp))

        StatsItem(
            imageVector = Icons.Outlined.FileDownload,
            titleRes = R.string.downloads,
            value = downloads,
            modifier = Modifier.weight(0.33f)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RelatedCollectionsItem(
    collection: Collection,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Card(
        shape = RoundedCornerShape(16.dp),
        onClick = onClick,
        modifier = modifier
    ) {
        Box(contentAlignment = Alignment.Center) {
            val placeholderBitmap by produceState<Bitmap?>(initialValue = null) {
                value = withContext(Dispatchers.Default) {
                    BlurHashDecoder.decode(
                        blurHash = collection.coverPhoto?.blurHash,
                        width = 4,
                        height = 3
                    )
                }
            }

            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(collection.coverPhoto?.getUrlByQuality(quality = PhotoQuality.MEDIUM))
                    .crossfade(durationMillis = 1000)
                    .placeholder(placeholderBitmap?.toDrawable(context.resources))
                    .fallback(placeholderBitmap?.toDrawable(context.resources))
                    .error(ColorDrawable(collection.coverPhoto?.primaryColorInt ?: Color.Gray.toArgb()))
                    .build(),
                contentScale = ContentScale.Crop,
                contentDescription = null,
                modifier = Modifier
                    .size(
                        width = 150.dp,
                        height = 85.dp
                    )
                    .drawWithContent {
                        drawContent()
                        drawRect(Color.Black.copy(alpha = 0.5f))
                    }
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .size(
                        width = 150.dp,
                        height = 85.dp
                    )
                    .padding(horizontal = 8.dp)
            ) {
                Text(
                    text = collection.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = Color.White
                )

                Text(
                    text = stringResource(
                        id = R.string.topic_photos_formatted,
                        collection.totalPhotos.abbreviatedNumberString
                    ),
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = Color.White
                )
            }
        }
    }

}

@Composable
fun RelatedCollectionsRow(
    collections: List<Collection>,
    onCollectionSelected: (CollectionId) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues()
) {
    LazyRow(
        contentPadding = contentPadding,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
    ) {
        items(count = collections.size) { index ->
            val collection = collections[index]

            RelatedCollectionsItem(
                collection = collection,
                onClick = { onCollectionSelected(CollectionId(collection.id)) }
            )
        }
    }
}