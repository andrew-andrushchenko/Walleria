package com.andrii_a.walleria.ui.collections

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import com.andrii_a.walleria.R
import com.andrii_a.walleria.domain.models.collection.Collection
import com.andrii_a.walleria.ui.common.ScrollToTopLayout
import com.andrii_a.walleria.ui.common.SearchQuery
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CollectionsScreen(
    collections: Flow<PagingData<Collection>>,
    navigateToProfileScreen: () -> Unit,
    navigateToSearchScreen: (SearchQuery?) -> Unit
) {
    val lazyCollectionItems = collections.collectAsLazyPagingItems()

    val pullRefreshState = rememberPullRefreshState(
        refreshing = lazyCollectionItems.loadState.refresh is LoadState.Loading,
        onRefresh = lazyCollectionItems::refresh,
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
                bottom = WindowInsets.navigationBars.asPaddingValues()
                    .calculateBottomPadding() + 100.dp
            )
        ) {
            CollectionsList(
                lazyCollectionItems = lazyCollectionItems,
                onCollectionClicked = {

                },
                onUserProfileClicked = {

                },
                onPhotoClicked = {

                },
                listState = listState,
                contentPadding = PaddingValues(
                    top = WindowInsets.systemBars.asPaddingValues().calculateTopPadding() + 64.dp,
                    bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding() + 200.dp
                )
            )
        }

        PullRefreshIndicator(
            refreshing = lazyCollectionItems.loadState.refresh is LoadState.Loading,
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
            Text(
                text = stringResource(id = R.string.all_collections),
                style = MaterialTheme.typography.h6,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
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