package com.andrii_a.walleria.ui.topic_details

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.andrii_a.walleria.domain.TopicStatus
import com.andrii_a.walleria.domain.models.topic.Topic
import com.andrii_a.walleria.domain.models.user.User
import com.andrii_a.walleria.ui.common.components.UserRowWithPhotoCount
import com.andrii_a.walleria.ui.common.components.lists.StatusIndicatorText
import com.andrii_a.walleria.ui.theme.WalleriaTheme

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
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
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