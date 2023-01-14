package com.andrii_a.walleria.ui.photos

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import com.andrii_a.walleria.R
import com.andrii_a.walleria.core.PhotoListDisplayOrder
import com.andrii_a.walleria.domain.models.photo.Photo
import com.andrii_a.walleria.ui.common.PhotoId
import com.andrii_a.walleria.ui.common.ScrollToTopLayout
import com.andrii_a.walleria.ui.common.WTitleDropdown
import com.andrii_a.walleria.ui.util.titleRes
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PhotosScreen(
    photos: Flow<PagingData<Photo>>,
    order: PhotoListDisplayOrder,
    orderBy: (Int) -> Unit,
    navigateToProfileScreen: () -> Unit,
    navigateToPhotoDetailsScreen: (PhotoId) -> Unit
) {
    val lazyPhotoItems = photos.collectAsLazyPagingItems()

    val pullRefreshState = rememberPullRefreshState(
        refreshing = lazyPhotoItems.loadState.refresh is LoadState.Loading,
        onRefresh = lazyPhotoItems::refresh,
        refreshingOffset = WindowInsets.systemBars.asPaddingValues().calculateTopPadding() + 120.dp
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .pullRefresh(pullRefreshState)
    ) {
        val listState = rememberLazyListState()

        ScrollToTopLayout(
            listState = listState,
            contentPadding = PaddingValues(
                bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding() + 100.dp
            )
        ) {
            PhotosList(
                lazyPhotoItems = lazyPhotoItems,
                onPhotoClicked = { photoId ->
                    navigateToPhotoDetailsScreen(photoId)
                },
                onUserProfileClicked = {},
                listState = listState,
                contentPadding = PaddingValues(
                    top = WindowInsets.systemBars.asPaddingValues().calculateTopPadding() + 64.dp,
                    bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding() + 200.dp
                ),
                modifier = Modifier.fillMaxSize()
            )
        }

        PullRefreshIndicator(
            refreshing = lazyPhotoItems.loadState.refresh is LoadState.Loading,
            state = pullRefreshState,
            modifier = Modifier.align(Alignment.TopCenter)
        )

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .statusBarsPadding()
                .background(color = MaterialTheme.colors.primary.copy(alpha = 0.9f))
                .height(64.dp)
                .padding(start = 16.dp, end = 16.dp)
                .fillMaxWidth()
                .align(Alignment.TopCenter)
        ) {
            val optionStringResources = PhotoListDisplayOrder.values().toList().map { it.titleRes }

            WTitleDropdown(
                selectedTitleRes = order.titleRes,
                titleTemplateRes = R.string.photos_title_template,
                optionsStringRes = optionStringResources,
                onItemSelected = orderBy
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
