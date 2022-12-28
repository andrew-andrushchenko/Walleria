package com.andrii_a.walleria.ui.collections

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.paging.PagingData
import com.andrii_a.walleria.R
import com.andrii_a.walleria.domain.models.collection.Collection
import com.andrii_a.walleria.ui.common.ScrollToTopLayout
import kotlinx.coroutines.flow.Flow

@Composable
fun CollectionsScreen(
    collections: Flow<PagingData<Collection>>,
    navigateToProfileScreen: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        val listState = rememberLazyListState()

        ScrollToTopLayout(
            listState = listState,
            contentPadding = PaddingValues(bottom = 120.dp)
        ) {
            CollectionsList(
                pagingDataFlow = collections,
                onCollectionClicked = {

                },
                onUserProfileClicked = {

                },
                onPhotoClicked = {

                },
                listState = listState,
                contentPadding = PaddingValues(
                    top = WindowInsets.systemBars.asPaddingValues().calculateTopPadding() + 48.dp
                )
            )
        }


        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .statusBarsPadding()
                .background(color = MaterialTheme.colors.primary.copy(alpha = 0.9f))
                .height(48.dp)
                .padding(start = 16.dp, end = 16.dp)
                .fillMaxWidth()
                .align(Alignment.TopCenter)
        ) {
            Text(
                text = stringResource(id = R.string.all_collections),
                style = MaterialTheme.typography.h6,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

            IconButton(onClick = navigateToProfileScreen) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_user_outlined),
                    contentDescription = stringResource(
                        id = R.string.user_profile_image
                    )
                )
            }
        }
    }
}