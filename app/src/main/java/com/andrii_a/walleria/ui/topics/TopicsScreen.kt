package com.andrii_a.walleria.ui.topics

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
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import com.andrii_a.walleria.R
import com.andrii_a.walleria.domain.TopicsDisplayOrder
import com.andrii_a.walleria.domain.models.topic.Topic
import com.andrii_a.walleria.ui.common.components.ScrollToTopLayout
import com.andrii_a.walleria.ui.common.SearchQuery
import com.andrii_a.walleria.ui.common.TopicId
import com.andrii_a.walleria.ui.common.components.WTitleDropdown
import com.andrii_a.walleria.ui.common.components.lists.TopicsList
import com.andrii_a.walleria.ui.util.titleRes
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun TopicsScreen(
    topics: Flow<PagingData<Topic>>,
    order: TopicsDisplayOrder,
    orderBy: (Int) -> Unit,
    navigateToTopicDetails: (TopicId) -> Unit,
    navigateToProfileScreen: () -> Unit,
    navigateToSearchScreen: (SearchQuery?) -> Unit
) {
    val lazyTopicItems = topics.collectAsLazyPagingItems()

    val pullRefreshState = rememberPullRefreshState(
        refreshing = lazyTopicItems.loadState.refresh is LoadState.Loading,
        onRefresh = lazyTopicItems::refresh,
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
                    .calculateBottomPadding() + dimensionResource(id = R.dimen.navigation_bar_height) + 8.dp
            )
        ) {
            TopicsList(
                lazyTopicItems = lazyTopicItems,
                onClick = navigateToTopicDetails,
                listState = listState,
                contentPadding = PaddingValues(
                    top = WindowInsets.systemBars.asPaddingValues().calculateTopPadding() + 64.dp,
                    bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding() + 200.dp
                )
            )
        }

        PullRefreshIndicator(
            refreshing = lazyTopicItems.loadState.refresh is LoadState.Loading,
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
            val optionStringResources = TopicsDisplayOrder.values().toList().map { it.titleRes }

            WTitleDropdown(
                selectedTitleRes = order.titleRes,
                titleTemplateRes = R.string.topics_title_template,
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