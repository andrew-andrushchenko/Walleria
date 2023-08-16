package com.andrii_a.walleria.ui.collection_details

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.unit.dp
import com.andrii_a.walleria.domain.models.user.User
import com.andrii_a.walleria.ui.common.components.UserRowWithPhotoCount

@Composable
fun CollectionDescriptionHeader(
    owner: User?,
    description: String?,
    totalPhotos: Long,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        description?.let {
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
            user = owner,
            totalPhotos = totalPhotos,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}
