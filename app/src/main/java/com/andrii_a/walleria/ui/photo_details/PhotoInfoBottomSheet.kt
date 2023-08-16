package com.andrii_a.walleria.ui.photo_details

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.andrii_a.walleria.R
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
import com.andrii_a.walleria.ui.photo_details.components.*
import com.andrii_a.walleria.ui.theme.WalleriaTheme
import com.andrii_a.walleria.ui.util.*

@Composable
fun PhotoInfoBottomSheet(
    photo: Photo,
    contentPadding: PaddingValues = PaddingValues(),
    navigateToSearch: (SearchQuery) -> Unit,
    navigateToCollectionDetails: (CollectionId) -> Unit
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .padding(contentPadding)
            .verticalScroll(rememberScrollState())
    ) {
        photo.location?.let { location ->
            location.locationString?.let { locationString ->
                LocationRow(
                    locationString = locationString,
                    onLocationClick = { context.openLocationInMaps(location.position) },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        photo.description?.let {
            var isExpanded by remember { mutableStateOf(false) }

            Text(
                text = it,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = if (isExpanded) Int.MAX_VALUE else 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .align(Alignment.CenterHorizontally)
                    .animateContentSize()
                    .clickable { isExpanded = !isExpanded }
            )

            Spacer(modifier = Modifier.height(16.dp))
        }

        photo.tags?.let {
            TagsRow(
                tags = it,
                onTagClicked = { query ->
                    navigateToSearch(SearchQuery(query))
                },
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))
        }

        StatsRow(
            views = photo.views,
            likes = photo.likes,
            downloads = photo.downloads,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        photo.exif?.let {
            ExifGrid(
                exif = it,
                resolution = stringResource(
                    id = R.string.resolution_formatted,
                    photo.width,
                    photo.height
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )
        }

        Divider(
            modifier = Modifier.padding(16.dp)
        )

        photo.relatedCollections?.let {
            it.results?.let { collections ->
                Text(
                    text = stringResource(id = R.string.related_collections),
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                RelatedCollectionsRow(
                    collections = collections,
                    onCollectionSelected = navigateToCollectionDetails,
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))
            }
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
            color = "#E0E0E0",
            blurHash = "",
            views = 200,
            downloads = 200,
            likes = 10,
            likedByUser = false,
            description = "Lorem ipsum dolor sit amet.".repeat(12),
            exif = exif,
            location = location,
            tags = tags,
            relatedCollections = relatedCollections,
            currentUserCollections = null,
            sponsorship = null,
            urls = PhotoUrls("", "", "", "", ""),
            links = null,
            user = user
        )

        Surface {
            PhotoInfoBottomSheet(
                photo = photo,
                navigateToSearch = {},
                navigateToCollectionDetails = {}
            )
        }
    }
}

