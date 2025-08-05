package com.andrii_a.walleria.ui.photo_details

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.graphics.Bitmap
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material.icons.filled.RemoveRedEye
import androidx.compose.material3.Card
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toDrawable
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.request.error
import coil3.request.fallback
import coil3.request.placeholder
import com.andrii_a.walleria.R
import com.andrii_a.walleria.domain.PhotoQuality
import com.andrii_a.walleria.domain.models.collection.Collection
import com.andrii_a.walleria.domain.models.common.Tag
import com.andrii_a.walleria.domain.models.photo.Photo
import com.andrii_a.walleria.domain.models.photo.PhotoExif
import com.andrii_a.walleria.domain.models.photo.PhotoLocation
import com.andrii_a.walleria.domain.models.photo.PhotoUrls
import com.andrii_a.walleria.domain.models.photo.RelatedCollections
import com.andrii_a.walleria.domain.models.user.User
import com.andrii_a.walleria.ui.common.CollectionId
import com.andrii_a.walleria.ui.common.SearchQuery
import com.andrii_a.walleria.ui.common.components.TagsRow
import com.andrii_a.walleria.ui.theme.WalleriaTheme
import com.andrii_a.walleria.ui.util.BlurHashDecoder
import com.andrii_a.walleria.ui.util.abbreviatedNumberString
import com.andrii_a.walleria.ui.util.createdDateTime
import com.andrii_a.walleria.ui.util.downloadFilename2
import com.andrii_a.walleria.ui.util.formCameraNameOrEmpty
import com.andrii_a.walleria.ui.util.getUrlByQuality
import com.andrii_a.walleria.ui.util.locationString
import com.andrii_a.walleria.ui.util.openLocationInMaps
import com.andrii_a.walleria.ui.util.primaryColorInt
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun PhotoInfoBottomSheet(
    photo: Photo,
    contentPadding: PaddingValues = PaddingValues(),
    navigateToSearch: (SearchQuery) -> Unit,
    navigateToCollectionDetails: (CollectionId) -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier
            .padding(contentPadding)
            .verticalScroll(rememberScrollState())
    ) {
        photo.tags?.let {
            TagsRow(
                tags = it,
                onTagClicked = { query ->
                    navigateToSearch(query)
                },
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp),
                modifier = Modifier.fillMaxWidth()
            )
        }

        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

        Text(
            text = photo.createdDateTime,
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        var isExpanded by remember { mutableStateOf(false) }

        Text(
            text = photo.description ?: stringResource(id = R.string.no_description_provided),
            style = MaterialTheme.typography.bodySmall,
            maxLines = if (isExpanded) Int.MAX_VALUE else 2,
            overflow = TextOverflow.Ellipsis,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { isExpanded = !isExpanded }
                .padding(horizontal = 16.dp)
                .align(Alignment.CenterHorizontally)
                .animateContentSize()
        )

        Text(
            text = stringResource(id = R.string.details),
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        PhotoInfo(
            photo = photo,
            modifier = Modifier.fillMaxWidth()
        )

        photo.relatedCollections?.let {
            it.results?.let { collections ->
                Text(
                    text = stringResource(id = R.string.related_collections),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                RelatedCollectionsRow(
                    collections = collections,
                    onCollectionSelected = navigateToCollectionDetails,
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp)
                )
            }
        }
    }
}

@Composable
fun PhotoInfoItem(
    title: String,
    text: AnnotatedString,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    clickable: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    ListItem(
        leadingContent = if (icon != null) {
            {
                Icon(
                    imageVector = icon,
                    contentDescription = null
                )
            }
        } else null,
        headlineContent = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        supportingContent = {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        trailingContent = if (clickable) {
            {
                FilledTonalIconButton(onClick = { onClick?.invoke() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Default.ArrowForward,
                        contentDescription = null
                    )
                }
            }
        } else null,
        colors = ListItemDefaults.colors(
            containerColor = Color.Transparent
        ),
        modifier = modifier
    )
}

@Composable
fun PhotoInfo(
    photo: Photo,
    modifier: Modifier = Modifier,
) {
    val unknown = stringResource(id = R.string.unknown)

    val context = LocalContext.current

    Column(modifier = modifier) {
        PhotoInfoItem(
            title = stringResource(id = R.string.location),
            text = buildAnnotatedString {
                append(photo.location?.locationString ?: stringResource(id = R.string.unknown))
            },
            icon = Icons.Filled.LocationOn,
            clickable = photo.location != null,
            onClick = {
                context.openLocationInMaps(photo.location?.position)
            }
        )

        PhotoInfoItem(
            title = photo.exif?.formCameraNameOrEmpty() ?: unknown,
            text = buildAnnotatedString {
                photo.exif?.aperture?.let {
                    withStyle(SpanStyle(fontStyle = FontStyle.Italic)) {
                        append("f/")
                    }
                    append(it)
                } ?: append(unknown)

                append(
                    stringResource(
                        id = R.string.exif_info,
                        photo.exif?.exposureTime ?: unknown,
                        photo.exif?.focalLength?.let { "${it}mm" } ?: unknown,
                        photo.exif?.iso?.let { "ISO$it" } ?: unknown
                    )
                )
            },
            icon = Icons.Filled.Camera
        )

        PhotoInfoItem(
            title = photo.downloadFilename2,
            text = buildAnnotatedString {
                append((photo.width * photo.height).abbreviatedNumberString)
                append("P")
                append(" \u2022 ")
                append(
                    stringResource(
                        id = R.string.resolution_formatted,
                        photo.width,
                        photo.height
                    )
                )
            },
            icon = Icons.Default.Photo
        )

        PhotoInfoItem(
            title = stringResource(id = R.string.likes),
            text = buildAnnotatedString { append(photo.likes.abbreviatedNumberString) },
            icon = Icons.Filled.Favorite
        )

        PhotoInfoItem(
            title = stringResource(id = R.string.views),
            text = buildAnnotatedString { append(photo.views.abbreviatedNumberString) },
            icon = Icons.Filled.RemoveRedEye
        )

        PhotoInfoItem(
            title = stringResource(id = R.string.downloads),
            text = buildAnnotatedString { append(photo.downloads.abbreviatedNumberString) },
            icon = Icons.Filled.CloudDownload
        )
    }
}

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
                    .error(
                        (collection.coverPhoto?.primaryColorInt ?: Color.Gray.toArgb()).toDrawable()
                    )
                    .build(),
                contentScale = ContentScale.Crop,
                contentDescription = null,
                modifier = Modifier
                    .size(
                        width = 120.dp,
                        height = 64.dp
                    )
                    .drawWithContent {
                        drawContent()
                        drawRect(Color.Black.copy(alpha = 0.5f))
                    }
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = collection.title,
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = Color.White
                )

                Text(
                    text = stringResource(
                        id = R.string.topic_photos_formatted,
                        collection.totalPhotos.abbreviatedNumberString
                    ),
                    style = MaterialTheme.typography.labelSmall,
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
                onClick = { onCollectionSelected(collection.id) }
            )
        }
    }
}

@Preview(uiMode = UI_MODE_NIGHT_NO)
@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
fun PhotoInfoBottomSheetPreview() {
    WalleriaTheme {
        val user = User(
            id = "",
            username = "ABC",
            firstName = "John",
            lastName = "Smith",
            bio = null,
            location = null,
            totalLikes = 0,
            totalPhotos = 0,
            totalCollections = 0,
            followersCount = 0,
            followingCount = 0,
            downloads = 0,
            profileImage = null,
            social = null,
            tags = null,
            photos = null
        )

        val exif = PhotoExif(
            make = "Google",
            model = "Pixel 4a",
            exposureTime = "1/400",
            aperture = "2.8",
            focalLength = "58.0",
            iso = 100
        )

        val location = PhotoLocation(
            city = "San Francisco",
            country = "California, USA",
            position = null
        )

        val tags = "Lorem ipsum dolor sit amet another very interesting text"
            .split(" ")
            .map { Tag(it) }

        val list = mutableListOf<Collection>()
        repeat(5) {
            list += Collection(
                id = "",
                title = "Title $it",
                description = null,
                curated = false,
                featured = false,
                totalPhotos = it.toLong(),
                isPrivate = false,
                tags = null,
                coverPhoto = null,
                previewPhotos = null,
                links = null,
                user = null
            )
        }

        val relatedCollections = RelatedCollections(
            results = list
        )

        val photo = Photo(
            id = "",
            width = 200,
            height = 300,
            createdAt = "2023-05-03T11:00:28Z",
            color = "#E0E0E0",
            blurHash = "",
            views = 234200,
            downloads = 200,
            likes = 134000,
            likedByUser = false,
            description = "Lorem ipsum dolor sit amet.".repeat(12),
            exif = exif,
            location = location,
            tags = tags,
            relatedCollections = relatedCollections,
            currentUserCollections = null,
            sponsorship = null,
            urls = PhotoUrls("", "https://images.unsplash.com/photo-1417325384643-aac51acc9e5d?q=75&fm=jpg", "", "", ""),
            links = null,
            user = user
        )

        Surface {
            PhotoInfoBottomSheet(
                photo = photo,
                navigateToSearch = {},
                navigateToCollectionDetails = {},
            )
        }
    }
}

