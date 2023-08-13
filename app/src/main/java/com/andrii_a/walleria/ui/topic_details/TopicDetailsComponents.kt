package com.andrii_a.walleria.ui.topic_details

import android.graphics.drawable.ColorDrawable
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.andrii_a.walleria.R
import com.andrii_a.walleria.domain.TopicStatus
import com.andrii_a.walleria.domain.models.topic.Topic
import com.andrii_a.walleria.domain.models.user.User
import com.andrii_a.walleria.ui.common.components.lists.StatusIndicatorText
import com.andrii_a.walleria.ui.theme.WalleriaTheme
import com.andrii_a.walleria.ui.util.abbreviatedNumberString
import com.andrii_a.walleria.ui.util.userFullName

@Composable
fun TopicDetailsDescriptionHeader(
    topic: Topic,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        topic.description?.let {
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
            user = topic.owners?.first(),
            totalPhotos = topic.totalPhotos,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(16.dp))

        StatusIndicatorText(
            status = topic.status,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}

// TODO(Andrii): move to common components to get rid of code duplicates with CollectionDetails
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

@Preview
@Composable
fun TopicDetailsDescriptionHeaderPreview() {
    WalleriaTheme {
        val user = User(
            id = "",
            username = "ABC",
            firstName = "John",
            lastName = "Smith",
            bio = "",
            location = "",
            totalLikes = 100,
            totalPhotos = 100,
            totalCollections = 100,
            followersCount = 100_000,
            followingCount = 56,
            downloads = 99_000,
            profileImage = null,
            social = null,
            tags = null,
            photos = null
        )

        val topic = Topic(
            id = "",
            title = "Wallpapers",
            description = "Lorem ipsum dolor sit amet".repeat(10),
            featured = false,
            startsAt = "",
            endsAt = "",
            updatedAt = "",
            totalPhotos = 856_000,
            links = null,
            status = TopicStatus.OPEN,
            owners = listOf(user),
            coverPhoto = null,
            previewPhotos = null
        )

        TopicDetailsDescriptionHeader(
            topic = topic,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp)
        )
    }
}