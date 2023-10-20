package com.andrii_a.walleria.ui.search

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.andrii_a.walleria.R
import com.andrii_a.walleria.domain.SearchResultsContentFilter
import com.andrii_a.walleria.domain.SearchResultsDisplayOrder
import com.andrii_a.walleria.domain.SearchResultsPhotoColor
import com.andrii_a.walleria.domain.SearchResultsPhotoOrientation
import com.andrii_a.walleria.ui.common.components.SelectorItemType
import com.andrii_a.walleria.ui.common.components.SingleChoiceSelector
import com.andrii_a.walleria.ui.common.components.SingleChoiceSelectorItem
import com.andrii_a.walleria.ui.theme.WalleriaTheme
import com.andrii_a.walleria.ui.util.iconRes
import com.andrii_a.walleria.ui.util.titleRes

@Composable
fun SearchPhotoFiltersBottomSheet(
    photoFilters: PhotoFilters,
    onApplyClick: (SearchScreenEvent) -> Unit,
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

                onApplyClick(SearchScreenEvent.ChangePhotoFilters(filters))
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

@Preview(uiMode = UI_MODE_NIGHT_NO)
@Preview(uiMode = UI_MODE_NIGHT_YES)
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
                onApplyClick = {},
                onDismiss = {}
            )
        }
    }
}