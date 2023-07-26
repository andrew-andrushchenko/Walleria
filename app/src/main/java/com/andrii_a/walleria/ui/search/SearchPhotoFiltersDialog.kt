package com.andrii_a.walleria.ui.search

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.andrii_a.walleria.R
import com.andrii_a.walleria.core.SearchResultsContentFilter
import com.andrii_a.walleria.core.SearchResultsDisplayOrder
import com.andrii_a.walleria.core.SearchResultsPhotoColor
import com.andrii_a.walleria.core.SearchResultsPhotoOrientation
import com.andrii_a.walleria.ui.common.SelectorItemType
import com.andrii_a.walleria.ui.common.SingleChoiceSelector
import com.andrii_a.walleria.ui.common.SingleChoiceSelectorItem
import com.andrii_a.walleria.ui.util.iconRes
import com.andrii_a.walleria.ui.util.titleRes

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SearchPhotoFilterDialog(
    photoFilters: State<PhotoFilters>,
    onApplyClick: (SearchScreenEvent) -> Unit,
    onDismiss: () -> Unit
) {
    var order by rememberSaveable { mutableStateOf(photoFilters.value.order) }
    var contentFilter by rememberSaveable { mutableStateOf(photoFilters.value.contentFilter) }
    var color by rememberSaveable { mutableStateOf(photoFilters.value.color) }
    var orientation by rememberSaveable { mutableStateOf(photoFilters.value.orientation) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colors.primary
        ) {
            Box(contentAlignment = Alignment.Center) {
                var dropdownExpanded by remember { mutableStateOf(false) }

                Column(
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = stringResource(id = R.string.photo_filters),
                        style = MaterialTheme.typography.h6,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )

                    Text(
                        text = stringResource(id = R.string.order),
                        style = MaterialTheme.typography.subtitle2
                    )

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
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colors.onPrimary,
                                shape = RoundedCornerShape(50)
                            )
                    )

                    Text(
                        text = stringResource(id = R.string.content_filter),
                        style = MaterialTheme.typography.subtitle2
                    )

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
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colors.onPrimary,
                                shape = RoundedCornerShape(50)
                            )
                    )

                    Text(
                        text = stringResource(id = R.string.orientation),
                        style = MaterialTheme.typography.subtitle2
                    )

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
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colors.onPrimary,
                                shape = RoundedCornerShape(50)
                            )
                    )

                    Text(
                        text = stringResource(id = R.string.filter_color),
                        style = MaterialTheme.typography.subtitle2
                    )

                    ExposedDropdownMenuBox(
                        expanded = dropdownExpanded,
                        onExpandedChange = {
                            dropdownExpanded = !dropdownExpanded
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            readOnly = true,
                            value = stringResource(id = color.titleRes),
                            onValueChange = { },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownExpanded)
                            },
                            shape = RoundedCornerShape(50),
                            modifier = Modifier.fillMaxWidth(),
                            colors = TextFieldDefaults.textFieldColors(
                                backgroundColor = Color.Transparent,
                                cursorColor = MaterialTheme.colors.onPrimary,
                                disabledLabelColor = Color.Gray,
                                focusedIndicatorColor = MaterialTheme.colors.onPrimary,
                                unfocusedIndicatorColor = MaterialTheme.colors.onPrimary,
                                textColor = MaterialTheme.colors.onPrimary
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

                    Button(
                        onClick = {
                            val filters = PhotoFilters(
                                order = order,
                                contentFilter = contentFilter,
                                color = color,
                                orientation = orientation
                            )

                            onApplyClick(SearchScreenEvent.OnPhotoFiltersChanged(filters))
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = MaterialTheme.colors.onPrimary,
                            contentColor = MaterialTheme.colors.primary
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = stringResource(id = R.string.apply),
                            style = MaterialTheme.typography.subtitle1
                        )
                    }
                }
            }
        }
    }
}