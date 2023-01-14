package com.andrii_a.walleria.ui.photo_details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.andrii_a.walleria.R
import com.andrii_a.walleria.domain.models.photo.Photo
import com.andrii_a.walleria.ui.common.SearchQuery
import com.andrii_a.walleria.ui.common.UserNickname
import com.andrii_a.walleria.ui.photo_details.components.*
import com.andrii_a.walleria.ui.util.*

@Composable
fun PhotoInfoBottomSheet(
    photo: Photo,
    navigateToUserDetails: (UserNickname) -> Unit,
    navigateToSearch: (SearchQuery) -> Unit
) {
    val context = LocalContext.current

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
    ) {
        Spacer(
            modifier = Modifier
                .padding(top = 12.dp)
                .size(width = 50.dp, height = 4.dp)
                .background(
                    color = MaterialTheme.colors.onPrimary,
                    shape = RoundedCornerShape(16.dp)
                )
                .align(Alignment.CenterHorizontally)
        )

        BigUserRow(
            userProfileImageUrl = photo.user?.getProfileImageUrlOrEmpty().orEmpty(),
            username = photo.userFullName,
            onUserClick = { navigateToUserDetails(UserNickname(photo.userNickname)) },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        photo.location?.let { location ->
            location.locationString?.let { locationString ->
                LocationRow(
                    locationString = locationString,
                    onLocationClick = { context.openLocationInMaps(location.position) },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }

        photo.tags?.let {
            TagsRow(
                tags = it,
                onTagClicked = { query ->
                    navigateToSearch(SearchQuery(query))
                },
                modifier = Modifier.fillMaxWidth()
            )
        }

        photo.description?.let {
            DescriptionColumn(
                description = it,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )
        }

        StatsRow(
            views = photo.views ?: 0,
            likes = photo.likes ?: 0,
            downloads = photo.downloads ?: 0,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )

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

        photo.relatedCollections?.let {
            it.results?.let { collections ->
                Text(
                    text = stringResource(id = R.string.related_collections),
                    style = MaterialTheme.typography.subtitle1,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                RelatedCollectionsRow(collections = collections)
            }
        }
    }
}

