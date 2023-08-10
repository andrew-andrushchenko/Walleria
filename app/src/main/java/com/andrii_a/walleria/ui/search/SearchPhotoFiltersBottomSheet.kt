package com.andrii_a.walleria.ui.search

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.andrii_a.walleria.R
import com.andrii_a.walleria.core.SearchResultsContentFilter
import com.andrii_a.walleria.core.SearchResultsDisplayOrder
import com.andrii_a.walleria.core.SearchResultsPhotoColor
import com.andrii_a.walleria.core.SearchResultsPhotoOrientation
import com.andrii_a.walleria.ui.common.components.SelectorItemType
import com.andrii_a.walleria.ui.common.components.SingleChoiceSelector
import com.andrii_a.walleria.ui.common.components.SingleChoiceSelectorItem
import com.andrii_a.walleria.ui.common.components.WButton
import com.andrii_a.walleria.ui.theme.WalleriaTheme
import com.andrii_a.walleria.ui.util.iconRes
import com.andrii_a.walleria.ui.util.titleRes

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SearchPhotoFiltersBottomSheet(
    photoFilters: PhotoFilters,
    onApplyClick: (SearchScreenEvent) -> Unit,
    onDismiss: () -> Unit
) {
    var order by rememberSaveable { mutableStateOf(photoFilters.order) }
    var contentFilter by rememberSaveable { mutableStateOf(photoFilters.contentFilter) }
    var color by rememberSaveable { mutableStateOf(photoFilters.color) }
    var orientation by rememberSaveable { mutableStateOf(photoFilters.orientation) }

    Surface(
        color = MaterialTheme.colors.surface,
        contentColor = MaterialTheme.colors.onSurface,
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
    ) {
        var dropdownExpanded by remember { mutableStateOf(false) }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(
                modifier = Modifier
                    .padding(vertical = 22.dp)
                    .size(width = 32.dp, height = 4.dp)
                    .background(
                        color = MaterialTheme.colors.onSurface.copy(alpha = 0.4f),
                        shape = RoundedCornerShape(50)
                    )
                    .align(Alignment.CenterHorizontally)
            )

            Text(
                text = stringResource(id = R.string.order),
                style = MaterialTheme.typography.subtitle1,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            SingleChoiceSelector(
                options = SearchResultsDisplayOrder.values().map {
                    SingleChoiceSelectorItem(titleRes = it.titleRes)
                },
                selectedOptionOrdinal = order.ordinal,
                onOptionSelect = { orderOrdinal ->
                    order = SearchResultsDisplayOrder.values()[orderOrdinal]
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .padding(horizontal = 16.dp)
                    .border(
                        width = 2.dp,
                        color = MaterialTheme.colors.onSurface,
                        shape = RoundedCornerShape(50)
                    )
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(id = R.string.content_filter),
                style = MaterialTheme.typography.subtitle1,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            SingleChoiceSelector(
                options = SearchResultsContentFilter.values().map {
                    SingleChoiceSelectorItem(titleRes = it.titleRes)
                },
                selectedOptionOrdinal = contentFilter.ordinal,
                onOptionSelect = { contentFilterOrdinal ->
                    contentFilter =
                        SearchResultsContentFilter.values()[contentFilterOrdinal]
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .padding(horizontal = 16.dp)
                    .border(
                        width = 2.dp,
                        color = MaterialTheme.colors.onSurface,
                        shape = RoundedCornerShape(50)
                    )
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(id = R.string.orientation),
                style = MaterialTheme.typography.subtitle1,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            SingleChoiceSelector(
                options = SearchResultsPhotoOrientation.values().mapIndexed { index, item ->
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
                    orientation = SearchResultsPhotoOrientation.values()[orderOrdinal]
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .padding(horizontal = 16.dp)
                    .border(
                        width = 2.dp,
                        color = MaterialTheme.colors.onSurface,
                        shape = RoundedCornerShape(50)
                    )
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(id = R.string.filter_color),
                style = MaterialTheme.typography.subtitle1,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            ExposedDropdownMenuBox(
                expanded = dropdownExpanded,
                onExpandedChange = {
                    dropdownExpanded = !dropdownExpanded
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                OutlinedTextField(
                    readOnly = true,
                    value = stringResource(id = color.titleRes),
                    onValueChange = { },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownExpanded)
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(50),
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = Color.Transparent,
                        cursorColor = MaterialTheme.colors.onSurface,
                        disabledLabelColor = Color.Gray,
                        focusedIndicatorColor = MaterialTheme.colors.onSurface,
                        unfocusedIndicatorColor = MaterialTheme.colors.onSurface,
                        textColor = MaterialTheme.colors.onSurface
                    )
                )

                ExposedDropdownMenu(
                    expanded = dropdownExpanded,
                    onDismissRequest = {
                        dropdownExpanded = false
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    SearchResultsPhotoColor.values().forEach { colorOption ->
                        DropdownMenuItem(
                            onClick = {
                                color = colorOption
                                dropdownExpanded = false
                            }
                        ) {
                            Text(text = stringResource(id = colorOption.titleRes))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            WButton(
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
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = MaterialTheme.colors.onSurface,
                    contentColor = MaterialTheme.colors.surface
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.apply),
                    style = MaterialTheme.typography.subtitle1
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Preview(uiMode = UI_MODE_NIGHT_NO)
@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
fun SearchPhotoFiltersBottomSheetPreview() {
    WalleriaTheme {
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