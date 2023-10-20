package com.andrii_a.walleria.ui.topics

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import com.andrii_a.walleria.R
import com.andrii_a.walleria.domain.TopicsDisplayOrder
import com.andrii_a.walleria.domain.models.topic.Topic
import com.andrii_a.walleria.ui.common.SearchQuery
import com.andrii_a.walleria.ui.common.TopicId
import com.andrii_a.walleria.ui.common.components.WTitleDropdown
import com.andrii_a.walleria.ui.common.components.lists.TopicsList
import com.andrii_a.walleria.ui.util.titleRes
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalMaterial3Api::class)
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

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    val optionStringResources =
                        TopicsDisplayOrder.entries.map { it.titleRes }

                    WTitleDropdown(
                        selectedTitleRes = order.titleRes,
                        titleTemplateRes = R.string.topics_title_template,
                        optionsStringRes = optionStringResources,
                        onItemSelected = orderBy
                    )
                },
                actions = {
                    IconButton(onClick = { navigateToSearchScreen(null) }) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = stringResource(
                                id = R.string.search
                            )
                        )
                    }

                    IconButton(onClick = navigateToProfileScreen) {
                        Icon(
                            imageVector = Icons.Outlined.AccountCircle,
                            contentDescription = stringResource(
                                id = R.string.user_profile_image
                            )
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
            )
        },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { innerPadding ->
        val listState = rememberLazyListState()

        TopicsList(
            lazyTopicItems = lazyTopicItems,
            onClick = navigateToTopicDetails,
            addNavigationBarPadding = true,
            listState = listState,
            contentPadding = PaddingValues(
                start = innerPadding.calculateStartPadding(LayoutDirection.Ltr),
                end = innerPadding.calculateEndPadding(LayoutDirection.Ltr),
                top = innerPadding.calculateTopPadding(),
                bottom = innerPadding.calculateBottomPadding() +
                        dimensionResource(id = R.dimen.navigation_bar_height) * 2
            )
        )
    }
}