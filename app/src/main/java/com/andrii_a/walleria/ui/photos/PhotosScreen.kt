package com.andrii_a.walleria.ui.photos

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
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
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import com.andrii_a.walleria.R
import com.andrii_a.walleria.domain.PhotoListDisplayOrder
import com.andrii_a.walleria.domain.PhotoQuality
import com.andrii_a.walleria.domain.PhotosListLayoutType
import com.andrii_a.walleria.domain.models.photo.Photo
import com.andrii_a.walleria.ui.common.*
import com.andrii_a.walleria.ui.common.components.WTitleDropdown
import com.andrii_a.walleria.ui.common.components.lists.PhotosGrid
import com.andrii_a.walleria.ui.common.components.lists.PhotosList
import com.andrii_a.walleria.ui.util.titleRes
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PhotosScreen(
    photos: Flow<PagingData<Photo>>,
    order: PhotoListDisplayOrder,
    photosListLayoutType: PhotosListLayoutType,
    photosLoadQuality: PhotoQuality,
    orderBy: (Int) -> Unit,
    navigateToProfileScreen: () -> Unit,
    navigateToSearchScreen: (SearchQuery?) -> Unit,
    navigateToPhotoDetailsScreen: (PhotoId) -> Unit,
    navigateToUserDetails: (UserNickname) -> Unit
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
        when (photosListLayoutType) {
            PhotosListLayoutType.DEFAULT -> {
                val listState = rememberLazyListState()

                PhotosList(
                    lazyPhotoItems = lazyPhotoItems,
                    onPhotoClicked = { photoId ->
                        navigateToPhotoDetailsScreen(photoId)
                    },
                    onUserProfileClicked = navigateToUserDetails,
                    isCompact = false,
                    addNavBarPadding = true,
                    photosQuality = photosLoadQuality,
                    listState = listState,
                    contentPadding = PaddingValues(
                        top = WindowInsets.systemBars.asPaddingValues().calculateTopPadding() + 64.dp,
                        bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding() + 200.dp
                    ),
                    modifier = Modifier.fillMaxSize()
                )
            }

            PhotosListLayoutType.MINIMAL_LIST -> {
                val listState = rememberLazyListState()

                PhotosList(
                    lazyPhotoItems = lazyPhotoItems,
                    onPhotoClicked = { photoId ->
                        navigateToPhotoDetailsScreen(photoId)
                    },
                    onUserProfileClicked = navigateToUserDetails,
                    isCompact = true,
                    addNavBarPadding = true,
                    photosQuality = photosLoadQuality,
                    listState = listState,
                    contentPadding = PaddingValues(
                        top = WindowInsets.systemBars.asPaddingValues().calculateTopPadding() + 64.dp,
                        bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding() + 200.dp
                    ),
                    modifier = Modifier.fillMaxSize()
                )
            }

            PhotosListLayoutType.STAGGERED_GRID -> {
                val gridState = rememberLazyStaggeredGridState()

                PhotosGrid(
                    lazyPhotoItems = lazyPhotoItems,
                    onPhotoClicked = navigateToPhotoDetailsScreen,
                    photosQuality = photosLoadQuality,
                    addNavBarPadding = true,
                    gridState = gridState,
                    contentPadding = PaddingValues(
                        top = WindowInsets.systemBars.asPaddingValues().calculateTopPadding() + 64.dp,
                        bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding() + 200.dp,
                        start = 8.dp,
                        end = 8.dp
                    ),
                    modifier = Modifier.fillMaxSize()
                )
            }
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
                .height(dimensionResource(id = R.dimen.top_bar_height))
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

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navigateToSearchScreen(null) }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_search_outlined),
                        contentDescription = stringResource(
                            id = R.string.search
                        )
                    )
                }

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
}
