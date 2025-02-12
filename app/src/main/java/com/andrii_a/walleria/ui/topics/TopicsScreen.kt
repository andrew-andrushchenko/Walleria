package com.andrii_a.walleria.ui.topics

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.paging.compose.collectAsLazyPagingItems
import com.andrii_a.walleria.R
import com.andrii_a.walleria.domain.TopicsDisplayOrder
import com.andrii_a.walleria.ui.common.components.TopicsGridContent
import com.andrii_a.walleria.ui.common.components.WTitleDropdown
import com.andrii_a.walleria.ui.util.titleRes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopicsScreen(
    state: TopicsUiState,
    onEvent: (TopicsEvent) -> Unit
) {
    val lazyTopicItems by rememberUpdatedState(newValue = state.topics.collectAsLazyPagingItems())

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    val optionStringResources =
                        TopicsDisplayOrder.entries.map { it.titleRes }

                    WTitleDropdown(
                        selectedTitleRes = state.topicsDisplayOrder.titleRes,
                        titleTemplateRes = R.string.topics_title_template,
                        optionsStringRes = optionStringResources,
                        onItemSelected = { orderOptionOrdinalNum ->
                            onEvent(TopicsEvent.ChangeListOrder(orderOptionOrdinalNum))
                        }
                    )
                },
                actions = {
                    IconButton(onClick = { onEvent(TopicsEvent.SelectSearch) }) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = stringResource(
                                id = R.string.search
                            )
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
            )
        },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { innerPadding ->
        TopicsGridContent(
            topicItems = lazyTopicItems,
            onTopicClick = { onEvent(TopicsEvent.SelectTopic(it)) },
            contentPadding = innerPadding
        )
    }
}