package com.andrii_a.walleria.ui.topics

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.paging.PagingData
import com.andrii_a.walleria.R
import com.andrii_a.walleria.core.TopicsDisplayOrder
import com.andrii_a.walleria.domain.models.topic.Topic
import com.andrii_a.walleria.ui.common.WTitleDropdown
import com.andrii_a.walleria.ui.util.titleRes
import kotlinx.coroutines.flow.Flow

@Composable
fun TopicsScreen(
    topics: Flow<PagingData<Topic>>,
    order: TopicsDisplayOrder,
    orderBy: (Int) -> Unit
) {
    Box(modifier = Modifier.fillMaxWidth()) {
        TopicsList(
            pagingDataFlow = topics,
            onClick = {},
            contentPadding = PaddingValues(
                top = WindowInsets.systemBars.asPaddingValues().calculateTopPadding() + 48.dp,
                bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding() + 200.dp
            )
        )

        Row(
            modifier = Modifier
                .statusBarsPadding()
                .background(color = MaterialTheme.colors.primary.copy(alpha = 0.9f))
                .height(48.dp)
                .padding(start = 16.dp, end = 16.dp)
                .fillMaxWidth()
                .align(Alignment.TopCenter)
        ) {
            val optionStringResources = TopicsDisplayOrder.values().toList().map { it.titleRes }

            WTitleDropdown(
                title = stringResource(
                    id = R.string.topics_title_template,
                    stringResource(id = order.titleRes)
                ),
                optionsStringRes = optionStringResources,
                onItemSelected = orderBy
            )
        }
    }
}