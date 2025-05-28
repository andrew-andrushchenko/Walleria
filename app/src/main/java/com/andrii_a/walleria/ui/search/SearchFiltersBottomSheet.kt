package com.andrii_a.walleria.ui.search

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.andrii_a.walleria.R
import com.andrii_a.walleria.domain.SearchResultsContentFilter
import com.andrii_a.walleria.domain.SearchResultsDisplayOrder
import com.andrii_a.walleria.domain.SearchResultsPhotoColor
import com.andrii_a.walleria.domain.SearchResultsPhotoOrientation
import com.andrii_a.walleria.domain.models.search.RecentSearchItem
import com.andrii_a.walleria.ui.common.components.SelectorItemType
import com.andrii_a.walleria.ui.common.components.SingleChoiceSelector
import com.andrii_a.walleria.ui.common.components.SingleChoiceSelectorItem
import com.andrii_a.walleria.ui.theme.WalleriaTheme
import com.andrii_a.walleria.ui.util.iconRes
import com.andrii_a.walleria.ui.util.titleRes

@Composable
fun SearchPhotoFiltersBottomSheet(
    photoFilters: PhotoFilters,
    onEvent: (SearchEvent) -> Unit,
    contentPadding: PaddingValues = PaddingValues(),
    onDismiss: () -> Unit
) {
    var order by rememberSaveable { mutableStateOf(photoFilters.order) }
    var contentFilter by rememberSaveable { mutableStateOf(photoFilters.contentFilter) }
    var color by rememberSaveable { mutableStateOf(photoFilters.color) }
    var orientation by rememberSaveable { mutableStateOf(photoFilters.orientation) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(contentPadding)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = stringResource(id = R.string.order),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        SingleChoiceSelector(
            options = SearchResultsDisplayOrder.entries.map {
                SingleChoiceSelectorItem(titleRes = it.titleRes)
            },
            selectedOptionOrdinal = order.ordinal,
            onOptionSelect = { orderOrdinal ->
                order = SearchResultsDisplayOrder.entries[orderOrdinal]
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = 16.dp)
                .border(
                    width = 2.dp,
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(50)
                )
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(id = R.string.content_filter),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        SingleChoiceSelector(
            options = SearchResultsContentFilter.entries.map {
                SingleChoiceSelectorItem(titleRes = it.titleRes)
            },
            selectedOptionOrdinal = contentFilter.ordinal,
            onOptionSelect = { contentFilterOrdinal ->
                contentFilter =
                    SearchResultsContentFilter.entries[contentFilterOrdinal]
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = 16.dp)
                .border(
                    width = 2.dp,
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(50)
                )
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(id = R.string.orientation),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        SingleChoiceSelector(
            options = SearchResultsPhotoOrientation.entries.mapIndexed { index, item ->
                if (index == 0) {
                    SingleChoiceSelectorItem(titleRes = item.titleRes)
                } else {
                    SingleChoiceSelectorItem(
                        titleRes = item.titleRes,
                        iconRes = item.iconRes,
                        type = SelectorItemType.IconOnly
                    )
                }
            },
            selectedOptionOrdinal = orientation.ordinal,
            onOptionSelect = { orderOrdinal ->
                orientation = SearchResultsPhotoOrientation.entries[orderOrdinal]
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = 16.dp)
                .border(
                    width = 2.dp,
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(50)
                )
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(id = R.string.filter_color),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        val listState = rememberLazyListState()

        LaunchedEffect(key1 = Unit) {
            listState.animateScrollToItem(color.ordinal)
        }

        PhotoColorsList(
            selectedOption = color,
            onColorItemSelected = { color = it },
            listState = listState,
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp),
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val filters = PhotoFilters(
                    order = order,
                    contentFilter = contentFilter,
                    color = color,
                    orientation = orientation
                )

                onEvent(SearchEvent.ChangePhotoFilters(filters))
                onDismiss()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = stringResource(id = R.string.apply),
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@Composable
fun RecentSearchesList(
    recentSearches: List<RecentSearchItem>,
    onItemSelected: (RecentSearchItem) -> Unit,
    onDeleteItem: (RecentSearchItem) -> Unit,
    onDeleteAllItems: () -> Unit
) {
    LazyColumn {
        items(
            count = recentSearches.size,
            key = { index -> recentSearches[index].id }
        ) { index ->
            val recentSearchItem = recentSearches[index]

            ListItem(
                headlineContent = { Text(text = recentSearchItem.title) },
                leadingContent = {
                    Icon(
                        Icons.Default.History,
                        contentDescription = null
                    )
                },
                trailingContent = {
                    IconButton(onClick = { onDeleteItem(recentSearchItem) }) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = null
                        )
                    }
                },
                colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                modifier = Modifier
                    .clickable(onClick = { onItemSelected(recentSearchItem) })
                    .fillMaxWidth()
                    .padding(start = 16.dp)
            )
        }

        if (recentSearches.isNotEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    TextButton(
                        onClick = onDeleteAllItems,
                        modifier = Modifier.align(Alignment.CenterEnd)
                    ) {
                        Text(text = stringResource(id = R.string.clear_recent_searches))
                    }
                }
            }
        }
    }
}

@Composable
fun PhotoColorsList(
    selectedOption: SearchResultsPhotoColor,
    onColorItemSelected: (SearchResultsPhotoColor) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
    listState: LazyListState = rememberLazyListState()
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = contentPadding,
        state = listState,
        modifier = modifier.selectableGroup()
    ) {
        items(SearchResultsPhotoColor.entries) {
            PresetColorItem(
                colorItem = it,
                selected = it == selectedOption,
                onSelected = { onColorItemSelected(it) },
                modifier = Modifier
            )
        }
    }
}

@Composable
private fun PresetColorItem(
    modifier: Modifier = Modifier,
    colorItem: SearchResultsPhotoColor,
    selected: Boolean,
    onSelected: () -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceColorAtElevation(16.dp),
        shape = RoundedCornerShape(16.dp),
        onClick = onSelected,
        modifier = modifier
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(84.dp)
                .padding(12.dp)
                .drawBehind {
                    drawColorCircle(colorItem)
                }
        ) {
            AnimatedVisibility(
                visible = selected,
                enter = fadeIn() + scaleIn(),
                exit = fadeOut() + scaleOut()
            ) {
                Box(
                    modifier = Modifier
                        .drawBehind {
                            val color = if (colorItem == SearchResultsPhotoColor.WHITE) {
                                Color(0xFF252930)
                            } else {
                                Color(0xFFFDFDFD)
                            }

                            drawCircle(color)
                        }
                        .padding(6.dp)
                ) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = null,
                        tint = if (colorItem == SearchResultsPhotoColor.WHITE) {
                            Color(0xFFFDFDFD)
                        } else {
                            Color(0xFF252930)
                        },
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

private fun DrawScope.drawColorCircle(colorItem: SearchResultsPhotoColor) {
    when (colorItem) {
        SearchResultsPhotoColor.ANY -> {
            drawCircle(
                brush = Brush.linearGradient(
                    listOf(
                        Color(0xFFF36870),
                        Color(0xFFFF9636),
                        Color(0xFFF8EA8C),
                        Color(0xFF7CB46B),
                        Color(0xFF005B96),
                    )
                ),
                style = Stroke(15f)
            )
        }

        SearchResultsPhotoColor.BLACK_AND_WHITE -> {
            drawArc(
                color = Color(0xFFFDFDFD),
                startAngle = -90f,
                sweepAngle = 180f,
                useCenter = true,
            )

            drawArc(
                color = Color(0xFF252930),
                startAngle = 90f,
                sweepAngle = 180f,
                useCenter = true,
            )
        }

        SearchResultsPhotoColor.BLACK -> {
            drawCircle(Color(0xFF252930))
        }

        SearchResultsPhotoColor.WHITE -> {
            drawCircle(Color(0xFFFDFDFD))
        }

        SearchResultsPhotoColor.YELLOW -> {
            drawCircle(Color(0xFFF8EA8C))
        }

        SearchResultsPhotoColor.ORANGE -> {
            drawCircle(Color(0xFFFF9636))
        }

        SearchResultsPhotoColor.RED -> {
            drawCircle(Color(0xFFF36870))
        }

        SearchResultsPhotoColor.PURPLE -> {
            drawCircle(Color(0xFFC55FFC))
        }

        SearchResultsPhotoColor.MAGENTA -> {
            drawCircle(Color(0xFFFF0BAC))
        }

        SearchResultsPhotoColor.GREEN -> {
            drawCircle(Color(0xFF7CB46B))
        }

        SearchResultsPhotoColor.TEAL -> {
            drawCircle(Color(0xFF66B2B2))
        }

        SearchResultsPhotoColor.BLUE -> {
            drawCircle(Color(0xFF005B96))
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun SearchPhotoFiltersBottomSheetPreview() {
    WalleriaTheme {
        Surface {
            SearchPhotoFiltersBottomSheet(
                photoFilters = PhotoFilters(
                    order = SearchResultsDisplayOrder.RELEVANT,
                    contentFilter = SearchResultsContentFilter.LOW,
                    color = SearchResultsPhotoColor.ANY,
                    orientation = SearchResultsPhotoOrientation.ANY
                ),
                onEvent = {},
                onDismiss = {}
            )
        }
    }
}